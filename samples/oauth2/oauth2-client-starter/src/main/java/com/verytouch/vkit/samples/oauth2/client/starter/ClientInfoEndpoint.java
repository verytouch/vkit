package com.verytouch.vkit.samples.oauth2.client.starter;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class ClientInfoEndpoint {

    private final Environment environment;

    public ClientInfoEndpoint(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                cookie.setPath(environment.getProperty("server.servlet.context-path"));
            }
        }
        request.getSession().invalidate();
        response.sendRedirect(environment.getProperty("security.oauth2.client.logout-uri"));
    }

    @GetMapping("token")
    public ResponseEntity<String> token(Authentication authentication) {
        return new ResponseEntity<>(((OAuth2AuthenticationDetails) authentication.getDetails()).getTokenValue(), HttpStatus.OK);
    }

    @GetMapping("resource/info")
    public ResponseEntity<String> resourceInfo() {
        return new ResponseEntity<>(environment.getProperty("info"), HttpStatus.OK);
    }
}
