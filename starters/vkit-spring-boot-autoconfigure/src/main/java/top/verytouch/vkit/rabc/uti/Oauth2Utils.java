package top.verytouch.vkit.rabc.uti;

import top.verytouch.vkit.common.util.JsonUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * oauth2工具类
 *
 * @author verytouch
 * @since 2021/5/16 21:45
 */
public class Oauth2Utils {

    /**
     * 获取用户名
     */
    public static String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * 获取认证信息
     */
    public static OAuth2AuthenticationDetails getDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (OAuth2AuthenticationDetails) authentication.getDetails();
    }

    /**
     * 获取权限信息
     */
    public static List<String> getAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(auth -> ((GrantedAuthority) auth).getAuthority())
                .collect(Collectors.toList());
    }

    /**
     * 获取access_token
     */
    public static String getAccessToken() {
        return getDetails().getTokenValue();
    }

    /**
     * 获取jwt中自定义的信息
     */
    public static Map<String, Object> getJwtUserInfo() {
        String claims = JwtHelper.decode(getDetails().getTokenValue()).getClaims();
        return JsonUtils.fromJson(claims, HashMap.class);
    }
}
