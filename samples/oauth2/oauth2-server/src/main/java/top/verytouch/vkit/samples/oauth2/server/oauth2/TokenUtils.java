package top.verytouch.vkit.samples.oauth2.server.oauth2;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

public class TokenUtils {

    public static OAuth2Authentication getAuthentication() {
        return (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public static OAuth2AuthenticationDetails getDetails() {
        return (OAuth2AuthenticationDetails) getAuthentication().getDetails();
    }

    public static String getToken() {
        return getDetails().getTokenValue();
    }
}
