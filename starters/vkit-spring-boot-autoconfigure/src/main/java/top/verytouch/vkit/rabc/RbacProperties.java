package top.verytouch.vkit.rabc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

}
