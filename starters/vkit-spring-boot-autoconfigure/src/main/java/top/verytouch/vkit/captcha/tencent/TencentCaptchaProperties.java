package top.verytouch.vkit.captcha.tencent;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 腾讯验证码配置类
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@ConfigurationProperties(prefix = "vkit.captcha.tencent")
@Data
@Validated
public class TencentCaptchaProperties {

    private String endpoint = "captcha.tencentcloudapi.com";

    private Long captchaType = 9L;

    private Long appId;

    private String appKey;

    private String secretId;

    private String secretKey;

}
