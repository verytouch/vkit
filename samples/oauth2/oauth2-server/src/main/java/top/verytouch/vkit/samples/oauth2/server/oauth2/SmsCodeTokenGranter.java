package top.verytouch.vkit.samples.oauth2.server.oauth2;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

public class SmsCodeTokenGranter extends AbstractTokenGranter {

    private final UserService userService;

    public SmsCodeTokenGranter(UserService userService, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetails, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetails, requestFactory, "sms_code");
        this.userService = userService;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());

        String phone = parameters.get("phone");
        String code = parameters.get("code");

        if (!"1234".equals(code)) {
            throw new InvalidGrantException("验证码不正确");
        }

        UserDetails userDetails = userService.loadUserByPhone(phone);
        if (userDetails == null) {
            throw new InvalidGrantException("手机号不正确");
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
        authentication.setDetails(parameters);

        OAuth2Request oAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(oAuth2Request, authentication);
    }
}
