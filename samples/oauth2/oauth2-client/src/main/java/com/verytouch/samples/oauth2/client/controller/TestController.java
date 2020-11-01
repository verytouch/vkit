package com.verytouch.samples.oauth2.client.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("token")
    public Object token(Authentication authentication) {
        return ((OAuth2AuthenticationDetails) authentication.getDetails()).getTokenValue();
    }

    @GetMapping("resource/info")
    public Object resourceInfo() {
        return "我受ResourceServer保护，需要token";
    }

}