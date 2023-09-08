package top.verytouch.vkit.rbac.openid;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import top.verytouch.vkit.common.util.StringUtils;
import top.verytouch.vkit.rbac.RbacProperties;
import top.verytouch.vkit.rbac.oauth2.OauthException;

/**
 * 根据openId授权
 *
 * @author verytouch
 * @since 2021/5/13 15:07
 */
public class OpenIdTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "openid";
    private final OpenIdService openIdService;
    private final RbacProperties rbacProperties;

    public OpenIdTokenGranter(
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            OpenIdService openIdService,
            RbacProperties rbacProperties) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.openIdService = openIdService;
        this.rbacProperties = rbacProperties;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        String secret = tokenRequest.getRequestParameters().get(rbacProperties.getUsernameParameter());
        if (StringUtils.isBlank(secret)) {
            throw new OauthException("secret can not be null");
        }
        String openid = openIdService.get(secret);
        if (StringUtils.isBlank(openid)) {
            throw new OauthException("secret unauthorized");
        }
        UserDetails userDetails = openIdService.loadUserByOpenId(openid);
        if (userDetails == null) {
            throw new OauthException("not bind any account");
        }
        OpenIdAuthenticationToken userAuth = new OpenIdAuthenticationToken(openid, secret, userDetails.getAuthorities());
        OAuth2Authentication authentication = new OAuth2Authentication(tokenRequest.createOAuth2Request(client), userAuth);
        authentication.setAuthenticated(true);
        authentication.setDetails(userDetails);
        return authentication;
    }

}
