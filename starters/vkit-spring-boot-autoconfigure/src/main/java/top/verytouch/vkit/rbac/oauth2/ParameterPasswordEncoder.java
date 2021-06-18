package top.verytouch.vkit.rbac.oauth2;

/**
 * 用于传输过程中对密码加解密
 *
 * @author verytouch
 * @since 2021/5/13 15:06
 */
@SuppressWarnings("unused")
public interface ParameterPasswordEncoder {

    String encode(String password);

    String decode(String encodedPassword);
}
