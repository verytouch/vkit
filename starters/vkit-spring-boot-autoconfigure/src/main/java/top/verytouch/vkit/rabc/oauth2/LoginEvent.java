package top.verytouch.vkit.rabc.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 登录事件
 *
 * @author verytouch
 * @since 2021/5/26 10:26
 */
@Getter
@Setter
public class LoginEvent extends ApplicationEvent {

    private final boolean successful;
    private final UserDetails userDetails;
    private final Exception exception;

    public LoginEvent(boolean successful, UserDetails user, Exception exception) {
        super(successful);
        this.successful = successful;
        this.userDetails = user;
        this.exception = exception;
    }
}
