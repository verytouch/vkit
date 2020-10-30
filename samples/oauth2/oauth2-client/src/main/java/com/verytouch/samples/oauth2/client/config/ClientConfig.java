package com.verytouch.samples.oauth2.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
public class ClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(clientRegistration1());
    }

    private ClientRegistration clientRegistration1() {
        return ClientRegistration.withRegistrationId("test1")
                .clientId("test1")
                .clientSecret("123456")
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                .redirectUriTemplate("http://127.0.0.1:91/client/login/oauth2/code/test1")
                .clientName("客户端1")
                .tokenUri("http://localhost:90/oauth/token")
                .authorizationUri("http://localhost:90/oauth/authorize")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("all")
                .userNameAttributeName("username")
                .userInfoUri("http://localhost:90/oauth/user/loginInfo")
                .jwkSetUri("")
                .build();
    }
}
