package com.verytouch.vkit.samples.oauth2.server.controller;

import com.verytouch.vkit.samples.oauth2.server.oauth2.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private TokenStore tokenStore;

    @GetMapping("loginInfo")
    public Object loginInfo() {
        return tokenStore.readAccessToken(TokenUtils.getToken());
    }
}
