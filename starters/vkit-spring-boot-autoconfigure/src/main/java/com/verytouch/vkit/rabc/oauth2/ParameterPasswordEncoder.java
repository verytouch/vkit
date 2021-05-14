package com.verytouch.vkit.rabc.oauth2;

/**
 * 用于传输过程中对密码加解密
 *
 * @author verytouch
 * @since 2021/5/13 15:06
 */
public interface ParameterPasswordEncoder {

    String encode(String password);

    String decode(String encodedPassword);
}
