package top.verytouch.vkit.rbac.oauth2;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * 认证异常类
 *
 * @author verytouch
 * @since 2021/5/13 15:10
 */
@JsonSerialize(using = OauthExceptionSerializer.class)
public class OauthException extends OAuth2Exception {

    public OauthException(String msg) {
        super(msg);
    }
}
