package top.verytouch.vkit.rbac.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
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
import org.springframework.web.cors.CorsConfiguration;
import top.verytouch.vkit.rbac.RbacProperties;
import top.verytouch.vkit.rbac.oauth2.*;
import top.verytouch.vkit.rbac.openid.OpenIdService;
import top.verytouch.vkit.rbac.openid.OpenIdTokenEndpoint;
import top.verytouch.vkit.rbac.openid.OpenIdTokenGranter;
import top.verytouch.vkit.rbac.util.ApplicationContextUtils;

import java.time.Duration;
import java.util.*;

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
    @SuppressWarnings("all")
    private UserDetailsService userDetailsService;

    @Autowired
    @SuppressWarnings("all")
    private AnotherClientDetailsService anotherClientDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @SuppressWarnings("all")
    private ParameterPasswordEncoder parameterPasswordEncoder;

    @Autowired
    @SuppressWarnings("all")
    private AuthorizationCodeServices authorizationCodeServices;

    @Autowired
    @SuppressWarnings("all")
    private RbacProperties rbacProperties;

    @Autowired
    @SuppressWarnings("all")
    private JwtUserDetailsTokenEnhancer jwtUserDetailsTokenEnhancer;

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
                // 自定义模式
                .tokenGranter(tokenGranter(endpointsConfigurer))
                .accessTokenConverter(jwtAccessTokenConverter())
                .tokenStore(tokenStore())
                .reuseRefreshTokens(false)
                // authorization_code模式自定义code有效期
                .authorizationCodeServices(new InMemoryAuthorizationCodeService(Duration.ofMinutes(3)))
                .tokenEnhancer(tokenEnhancerChain())
                .authorizationCodeServices(authorizationCodeServices)
                .exceptionTranslator(e -> new ResponseEntity<>(new OauthException(e.getMessage()), HttpStatus.OK))
                .getFrameworkEndpointHandlerMapping().setCorsConfigurations(corsConfigurationMap());
    }


    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(rbacProperties.getJwtSingKey());
        return converter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            if (jwtUserDetailsTokenEnhancer == null || authentication.getUserAuthentication() == null) {
                return accessToken;
            }
            UserDetails details = null;
            if (authentication.getDetails() instanceof UserDetails) {
                details = (UserDetails) authentication.getDetails();
            } else {
                Authentication userAuth = authentication.getUserAuthentication();
                if (userAuth instanceof UserDetails) {
                    details = (UserDetails) authentication.getDetails();
                } else {
                    Object principal = userAuth.getPrincipal();
                    if (principal instanceof UserDetails) {
                        details = (UserDetails) principal;
                    }
                }
            }

            if (details != null) {
                Map<String, Object> additionalInfo = jwtUserDetailsTokenEnhancer.enhance(details);
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            }
            return accessToken;
        };
    }

    @Bean
    @ConditionalOnProperty(prefix = "vkit.rbac", name = "openid-token-granter-enabled", havingValue = "true")
    public OpenIdTokenEndpoint openIdTokenEndpoint(OpenIdService openIdService) {
        return new OpenIdTokenEndpoint(openIdService);
    }

    private TokenEnhancerChain tokenEnhancerChain() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter()));
        return tokenEnhancerChain;
    }

    private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> granters = new ArrayList<>(Collections.singletonList(endpoints.getTokenGranter()));
        // 用户名 + 密码 + 验证码
        granters.add(new CaptchaTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(),
                authenticationManager,
                parameterPasswordEncoder,
                rbacProperties
        ));
        // openid授权 + secretId查询授权
        if (rbacProperties.isOpenidTokenGranterEnabled()) {
            granters.add(new OpenIdTokenGranter(
                    endpoints.getTokenServices(),
                    endpoints.getClientDetailsService(),
                    endpoints.getOAuth2RequestFactory(),
                    ApplicationContextUtils.getBean(OpenIdService.class),
                    rbacProperties
            ));
        }
        return new CompositeTokenGranter(granters);
    }

    private Map<String, CorsConfiguration> corsConfigurationMap() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedMethod("*");
        Map<String, CorsConfiguration> corsConfigurationMap = new HashMap<>();
        corsConfigurationMap.put("/oauth/**", corsConfiguration);
        return corsConfigurationMap;
    }
}
