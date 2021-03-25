package com.verytouch.vkit.samples.oauth2.server.config;

import com.verytouch.vkit.samples.oauth2.server.oauth2.AdditionalUserInfoTokenConverter;
import com.verytouch.vkit.samples.oauth2.server.oauth2.AdditionalUserInfoTokenEnhancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

@Configuration
public class JwtConfig {

    @Bean
    public JwtAccessTokenConverter tokenConverter() {
        JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
        tokenConverter.setSigningKey("123456");
        tokenConverter.setAccessTokenConverter(new AdditionalUserInfoTokenConverter());
        return tokenConverter;
    }

    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter tokenConverter) {
        return new JwtTokenStore(tokenConverter);
    }

    @Bean
    public TokenEnhancerChain tokenEnhancerChain(JwtAccessTokenConverter tokenConverter) {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        enhancerChain.setTokenEnhancers(Arrays.asList(new AdditionalUserInfoTokenEnhancer(), tokenConverter));
        return enhancerChain;
    }
}
