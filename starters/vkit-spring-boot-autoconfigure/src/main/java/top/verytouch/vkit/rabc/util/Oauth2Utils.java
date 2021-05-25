package top.verytouch.vkit.rabc.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import top.verytouch.vkit.common.util.JsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * oauth2工具类
 *
 * @author verytouch
 * @since 2021/5/16 21:45
 */
public class Oauth2Utils {

    /**
     * 获取认证信息
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        return getAuthentication().getName();
    }

    /**
     * 获取权限信息
     */
    public static Set<String> getAuthorities() {
        return getAuthentication()
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    /**
     * 获取access_token
     */
    public static String getAccessToken() {
        return ((OAuth2AuthenticationDetails) getAuthentication().getDetails()).getTokenValue();
    }

    /**
     * 获取jwt中自定义的信息
     */
    public static Map<String, Object> getJwtClaims() {
        String claims = JwtHelper.decode(getAccessToken()).getClaims();
        return JsonUtils.fromJson(claims, HashMap.class);
    }

    /**
     * 获取token过期时间戳
     */
    public static long getJwtExpireAt() {
        return Long.parseLong(Objects.toString(getJwtClaims().get("exp"))) * 1000;
    }
}
