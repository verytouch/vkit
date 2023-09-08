package top.verytouch.vkit.rbac.openid;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * 根据openId授权
 *
 * @author verytouch
 * @since 2021/5/13 15:07
 */
public interface OpenIdService {

    UserDetails loadUserByOpenId(String openid);

    Object preAuthorize(String secret, Object platform);

    void store(String secret, String openid);

    String get(String secret);
}
