package top.verytouch.vkit.rbac.oauth2;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import top.verytouch.vkit.captcha.CaptchaContext;
import top.verytouch.vkit.common.exception.BusinessException;
import top.verytouch.vkit.common.util.StringUtils;
import top.verytouch.vkit.rbac.RbacProperties;

import java.util.Map;

/**
 * 用户名-密码-验证码来获取授权
 *
 * @author verytouch
 * @since 2021/5/13 15:07
 */
public class CaptchaTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "captcha";

    private final AuthenticationManager authenticationManager;

    private final ParameterPasswordEncoder parameterPasswordEncoder;

    private final RbacProperties rbacProperties;

    public CaptchaTokenGranter(
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            AuthenticationManager authenticationManager,
            ParameterPasswordEncoder parameterPasswordEncoder,
            RbacProperties rbacProperties) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
        this.parameterPasswordEncoder = parameterPasswordEncoder;
        this.rbacProperties = rbacProperties;
    }


    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = tokenRequest.getRequestParameters();
        // 比对验证码
        if (rbacProperties.isCaptchaEnabled()) {
            CaptchaContext.verify(parameters);
        }
        // 交给AuthenticationManager认证
        String username = getUsername(parameters);
        String password = getDecodedPassword(parameters);
        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException e) {
            throw new OauthException(e.getMessage());
        }
        if (userAuth != null && userAuth.isAuthenticated()) {
            OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
            return new OAuth2Authentication(storedOAuth2Request, userAuth);
        } else {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }
    }

    private String getUsername(Map<String, String> parameters) {
        String inputUsername = parameters.get(rbacProperties.getUsernameParameter());
        if (StringUtils.isBlank(inputUsername)) {
            throw new OauthException(rbacProperties.getInvalidAccountMsg());
        }
        return inputUsername;
    }

    private String getDecodedPassword(Map<String, String> parameters) {
        String inputPassword = parameters.get(rbacProperties.getPasswordParameter());
        if (StringUtils.isBlank(inputPassword)) {
            throw new OauthException(rbacProperties.getInvalidPasswordMsg());
        }
        try {
            return parameterPasswordEncoder.decode(inputPassword);
        } catch (BusinessException e) {
            throw new OauthException(rbacProperties.getInvalidPasswordMsg(), e);
        }

    }

}
