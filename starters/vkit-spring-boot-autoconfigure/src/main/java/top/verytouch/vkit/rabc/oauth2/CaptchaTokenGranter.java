package top.verytouch.vkit.rabc.oauth2;

import top.verytouch.vkit.captcha.CaptchaContext;
import top.verytouch.vkit.rabc.RbacProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

/**
 * 用户名-密码-验证码来获取授权
 *
 * @author verytouch
 * @since 2021/5/13 15:07
 */
public class CaptchaTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "captcha";

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final ParameterPasswordEncoder parameterPasswordEncoder;

    private final RbacProperties rbacProperties;

    public CaptchaTokenGranter(
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            UserDetailsService userDetailService,
            PasswordEncoder passwordEncoder,
            ParameterPasswordEncoder parameterPasswordEncoder,
            RbacProperties rbacProperties) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.userDetailsService = userDetailService;
        this.passwordEncoder = passwordEncoder;
        this.parameterPasswordEncoder = parameterPasswordEncoder;
        this.rbacProperties = rbacProperties;
    }


    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = tokenRequest.getRequestParameters();
        // 比对验证码
        verifyCode(parameters);
        // 比对用户名密码
        UserDetails userDetails = verifyUserNameAndPassword(parameters);
        // 认证成功
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername()
                , userDetails, userDetails.getAuthorities());
        OAuth2Authentication authentication = new OAuth2Authentication(tokenRequest.createOAuth2Request(client), auth);
        authentication.setDetails(userDetails);
        return authentication;
    }

    private UserDetails verifyUserNameAndPassword(Map<String, String> parameters) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(parameters.get(rbacProperties.getUsernameParameter()));
        String password = parameterPasswordEncoder.decode(parameters.get(rbacProperties.getPasswordParameter()));
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new OauthException("密码错误");
        }
        return userDetails;
    }

    private void verifyCode(Map<String, String> parameters) {
        if (rbacProperties.isCaptchaEnabled()) {
            CaptchaContext.verify(parameters);
        }
    }
}
