package com.verytouch.vkit.captcha.tencent;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "captcha.tencent")
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
