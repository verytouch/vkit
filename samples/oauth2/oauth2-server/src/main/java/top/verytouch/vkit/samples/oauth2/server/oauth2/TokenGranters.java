package top.verytouch.vkit.samples.oauth2.server.oauth2;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Arrays;
import java.util.List;

public class TokenGranters implements TokenGranter {

    private final List<TokenGranter> tokenGranters;
    private CompositeTokenGranter delegate;

    public TokenGranters(TokenGranter... tokenGranters) {
        this.tokenGranters = Arrays.asList(tokenGranters);
    }

    @Override
    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
        if (delegate == null) {
            delegate = new CompositeTokenGranter(tokenGranters);
        }
        return delegate.grant(grantType, tokenRequest);
    }

    public static TokenGranter defaultTokenGranters(
            AuthorizationServerEndpointsConfigurer configurer,
            AuthenticationManager authenticationManager,
            UserService userService) {
        ClientDetailsService clientDetails = configurer.getClientDetailsService();
        AuthorizationServerTokenServices tokenServices = configurer.getTokenServices();
        AuthorizationCodeServices authorizationCodeServices = configurer.getAuthorizationCodeServices();
        OAuth2RequestFactory requestFactory = configurer.getOAuth2RequestFactory();

        return new TokenGranters(
                new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetails, requestFactory),
                new RefreshTokenGranter(tokenServices, clientDetails, requestFactory),
                new ImplicitTokenGranter(tokenServices, clientDetails, requestFactory),
                new ClientCredentialsTokenGranter(tokenServices, clientDetails, requestFactory),
                new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices, clientDetails, requestFactory),
                new SmsCodeTokenGranter(userService, tokenServices, clientDetails, requestFactory)
        );
    }
}
