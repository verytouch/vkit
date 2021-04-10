package com.verytouch.vkit.captcha.tencent;

import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.verytouch.vkit.captcha.CaptchaContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(CaptchaClient.class)
@EnableConfigurationProperties(TencentCaptchaProperties.class)
public class TencentCaptchaAutoConfiguration {

    @Bean
    public TencentCaptchaService tencentCaptchaProperties(TencentCaptchaProperties tencentCaptchaProperties) {
        TencentCaptchaService tencentCaptchaService = new TencentCaptchaService(tencentCaptchaProperties);
        CaptchaContext.addService(tencentCaptchaService);
        return tencentCaptchaService;
    }
}
