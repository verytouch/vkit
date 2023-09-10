package top.verytouch.vkit.rbac.openid;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * 根据openId授权
 *
 * @author verytouch
 * @since 2021/5/13 15:07
 */
public interface OpenIdService {

    /**
     * 根据openid查询用户
     */
    UserDetails loadUserByOpenId(String openid);

    /**
     * 用来生成第三方认证方式，如微信二维码、github登录页等
     */
    String authorize(String secret);

    /**
     * 用code换取openid
     */
    String getOpenid(String code);

    /**
     * 用于secret换取code，小程序自己登录用不上
     */
    default void storeCode(String secret, String code) {

    }

    /**
     * 用于secret换取code，小程序自己登录用不上
     */
    default String getCode(String secret) {
        return null;
    }

}
