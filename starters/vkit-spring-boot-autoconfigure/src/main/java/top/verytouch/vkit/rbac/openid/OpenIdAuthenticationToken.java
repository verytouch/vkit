package top.verytouch.vkit.rbac.openid;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


/**
 * openId信息
 *
 * @author verytouch
 * @since 2021/5/13 15:07
 */
public class OpenIdAuthenticationToken extends AbstractAuthenticationToken {

    private final String openId;

    public OpenIdAuthenticationToken(String openId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.openId = openId;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return openId;
    }
}
