package com.verytouch.vkit.rabc.config;

import com.verytouch.vkit.rabc.RbacProperties;
import com.verytouch.vkit.rabc.oauth2.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 授权服务配置
 *
 * @author verytouch
 * @since 2021/5/14 17:42
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationSererConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AnotherClientDetailsService anotherClientDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ParameterPasswordEncoder parameterPasswordEncoder;

    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;

    @Autowired
    private RbacProperties rbacProperties;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer serverSecurityConfigurer) {
        serverSecurityConfigurer.allowFormAuthenticationForClients()
                .checkTokenAccess("permitAll()")
                .tokenKeyAccess("permitAll()")
                .accessDeniedHandler(OauthExceptionSerializer::exceptionHandler)
                .authenticationEntryPoint(OauthExceptionSerializer::exceptionHandler);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clientDetailsServiceConfigurer) throws Exception {
        clientDetailsServiceConfigurer.withClientDetails(anotherClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpointsConfigurer) {
        endpointsConfigurer.userDetailsService(userDetailsService)
                .authenticationManager(authenticationManager)
                .tokenGranter(tokenGranter(endpointsConfigurer))
                .accessTokenConverter(jwtAccessTokenConverter())
                .tokenStore(tokenStore())
                .reuseRefreshTokens(false)
                .authorizationCodeServices(new InMemoryAuthorizationCodeService(Duration.ofMinutes(3)))
                .tokenEnhancer(tokenEnhancerChain())
                .authorizationCodeServices(authorizationCodeServices)
                .exceptionTranslator(e -> new ResponseEntity<>(new OauthException(e.getMessage()), HttpStatus.OK));
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
        // return new JdbcTokenStore(dataSource);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(rbacProperties.getJwtSingKey());
        return converter;
    }

    @Bean
    public TokenEnhancer myTokenEnhancer() {
        return (accessToken, authentication) -> {
            UserDetails details = null;
            if (authentication.getDetails() instanceof UserDetails) {
                details = (UserDetails) authentication.getDetails();
            } else {
                Authentication userAuthentication = authentication.getUserAuthentication();
                if (userAuthentication instanceof UserDetails) {
                    details = (UserDetails) authentication.getDetails();
                } else {
                    Object principal = userAuthentication.getPrincipal();
                    if (principal instanceof UserDetails) {
                        details = (UserDetails) principal;
                    }
                }
            }

            if (details != null) {
                /*Map<String, Object> additionalInfo = new HashMap<>();
                additionalInfo.put("id", details.getUserInfo().getId());
                additionalInfo.put("name", details.getUserInfo().getName());
                additionalInfo.put("avatar", details.getUserInfo().getAvatar());
                additionalInfo.put("roles", details.getRoles().stream().map(SysRole::getName).collect(Collectors.toList()));
                additionalInfo.put("resources", details.getRoles().stream().flatMap(
                        r -> r.getResourceList().stream()
                ).map(SysResource::getResKey).collect(Collectors.toSet()));
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

                // 更新登录信息
                SysLoginLog loginLog = new SysLoginLog();
                loginLog.setUserId(details.getUserInfo().getId());
                loginLog.setUserName(details.getUserInfo().getName());
                loginLog.setSuccessful("Y");
                loginLog.setRemark("登录成功");
                myUserDetailService.saveLoginInfo(loginLog);*/
            }
            return accessToken;
        };
    }

    private TokenEnhancerChain tokenEnhancerChain() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(myTokenEnhancer(), jwtAccessTokenConverter()));
        return tokenEnhancerChain;
    }

    private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
        CaptchaTokenGranter captchaTokenGranter = new CaptchaTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(),
                userDetailsService,
                passwordEncoder,
                parameterPasswordEncoder,
                rbacProperties
        );
        List<TokenGranter> granters = new ArrayList<>(Arrays.asList(endpoints.getTokenGranter()));
        granters.add(captchaTokenGranter);
        return new CompositeTokenGranter(granters);
    }
}
