package com.verytouch.vkit.captcha.tencent;

import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.verytouch.vkit.captcha.CaptchaContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯验证码自动配置类
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Configuration
@ConditionalOnClass(CaptchaClient.class)
@EnableConfigurationProperties(TencentCaptchaProperties.class)
@Slf4j
public class TencentCaptchaAutoConfiguration {

    @Bean
    public TencentCaptchaService tencentCaptchaProperties(TencentCaptchaProperties tencentCaptchaProperties) {
        log.info("已启用TencentCaptcha自动配置");
        TencentCaptchaService tencentCaptchaService = new TencentCaptchaService(tencentCaptchaProperties);
        CaptchaContext.addService(tencentCaptchaService);
        return tencentCaptchaService;
    }
}
