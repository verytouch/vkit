package top.verytouch.vkit.rbac;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.time.Duration;
import java.util.Map;

/**
 * rbac配置类
 *
 * @author verytouch
 * @since 2021/5/14 17:49
 */
@ConfigurationProperties(prefix = "vkit.rbac")
@Data
@Validated
public class RbacProperties {

    /**
     * 是否启用自动配置
     */
    private boolean enabled = true;

    /**
     * 登录时加密传输密码的秘钥，16位
     */
    @NotEmpty(message = "parameterAesKey不能为空")
    private String parameterAesKey;

    /**
     * jwt签名秘钥
     */
    @NotEmpty(message = "jwtSingKey不能为空")
    private String jwtSingKey;

    /**
     * 获取用户名的参数名
     */
    private String usernameParameter = "account";

    /**
     * 获取密码的参数名
     */
    private String passwordParameter = "password";

    /**
     * 账号错误的提示信息
     */
    private final String invalidAccountMsg = "账号不存在或密码错误";

    /**
     * 密码错误的提示信息
     */
    private final String invalidPasswordMsg = "账号不存在或密码错误";

    /**
     * 资源服务器ID
     */
    private String resourceId;

    /**
     * 授权码存活时间
     */
    private Duration authorizationCodeDuration = Duration.ofMinutes(5);

    /**
     * 认证时是否校验验证码，仅authorization_type=captcha时生效
     */
    private boolean captchaEnabled = true;

    /**
     * 接口开放路径
     */
    private String[] openPath;

    /**
     * key对应的接口，需要values中任意权限方可访问
     */
    private Map<String, String[]> pathAnyAuthorityMapping;

    /**
     * 注入默认的异常处理器
     */
    private boolean exceptionHandlerEnabled = true;

    /**
     * 注入默认的接口日志打印
     */
    private boolean requestLogEnabled = true;

    /**
     * 是否提供openid授权
     */
    private boolean openidTokenGranterEnabled = false;

}
