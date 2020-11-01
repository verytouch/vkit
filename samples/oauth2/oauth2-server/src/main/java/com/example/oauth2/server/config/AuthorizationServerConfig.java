package com.example.oauth2.server.config;

import com.example.oauth2.server.oauth2.RedisAuthorizationCodeServices;
import com.example.oauth2.server.oauth2.TokenGranters;
import com.example.oauth2.server.oauth2.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private TokenEnhancerChain tokenEnhancerChain;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients()
                .checkTokenAccess("permitAll()")
                .tokenKeyAccess("permitAll()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        super.configure(clients);
        clients.inMemory()
                .withClient("test1")
                // 123456
                .secret("$2a$10$piCRbkfQUTAHjsbgXcoFrOSkuHTb7K8lGrTclGZvgjr.LK1ZWmUJa")
                .authorizedGrantTypes("password", "refresh_token", "authorization_code", "client_credentials", "implicit", "sms_code")
                .scopes("all")
                .autoApprove(true)
                .redirectUris("http://localhost:90/index.html", "http://localhost:91/client/login")
                .accessTokenValiditySeconds(30 * 60)
                .refreshTokenValiditySeconds(7 * 24 * 60 * 60);

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer configurer) {
        configurer.tokenStore(tokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)
                .tokenEnhancer(tokenEnhancerChain)
                // password模式必须
                .authenticationManager(authenticationManager)
                // refresh_token模式必须
                .userDetailsService(userDetailsService)
                // authorization_code模式自定义code有效期
                .authorizationCodeServices(new RedisAuthorizationCodeServices(redisConnectionFactory))
                // 自定义模式
                .tokenGranter(TokenGranters.defaultTokenGranters(configurer, authenticationManager, (UserService) userDetailsService));
    }

    // 自定义授权页面
    @Controller
    @SessionAttributes("authorizationRequest")
    public static class PageController {
        @RequestMapping("/oauth/confirm_access")
        public ModelAndView authPage(Map<String, Object> model, ModelAndView mv) {
            AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
            mv.addObject("clientId", authorizationRequest.getClientId());
            mv.addObject("scopes", authorizationRequest.getScope());
            mv.setViewName("authorize");
            return mv;
        }
    }

}
