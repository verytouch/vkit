package com.verytouch.vkit.captcha.image;

import com.verytouch.vkit.captcha.CaptchaContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 图形验证码自动配置类
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Configuration
@EnableConfigurationProperties(ImageCaptchaProperties.class)
@ConditionalOnBean(CaptchaCodeStore.class)
@Slf4j
public class ImageCaptchaAutoConfiguration {

    /*
     * 为了让图形验证码可选，此处不再注入默认的CaptchaCodeStore
     *
     * @Bean
     * @ConditionalOnMissingBean(CaptchaCodeStore.class)
     * public CaptchaCodeStore captchaCodeStore() {
     *     return new InMemoryCaptchaCodeStore();
     * }
     */

    @Bean
    public ImageCaptchaService imageCaptchaService(CaptchaCodeStore captchaCodeStore, ImageCaptchaProperties properties) {
        log.info("已启用ImageCaptcha自动配置");
        ImageCaptchaService imageCaptchaService = new ImageCaptchaService(captchaCodeStore, properties);
        CaptchaContext.addService(imageCaptchaService);
        return imageCaptchaService;
    }
}
