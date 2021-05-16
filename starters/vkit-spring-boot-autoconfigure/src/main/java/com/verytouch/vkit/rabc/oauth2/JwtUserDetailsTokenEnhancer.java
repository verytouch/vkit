package com.verytouch.vkit.rabc.oauth2;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * 用来在jwt中添加额外信息
 *
 * @author verytouch
 * @since 2021/5/16 23:30
 */
public interface JwtUserDetailsTokenEnhancer {

    /**
     * token增强
     *
     * @param userDetails UserDetailsService返回的UserDetails
     * @return 该map放入jwt中
     */
    Map<String, Object> enhance(UserDetails userDetails);
}
