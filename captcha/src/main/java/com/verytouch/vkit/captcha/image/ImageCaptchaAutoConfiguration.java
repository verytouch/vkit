package com.verytouch.vkit.captcha.image;

import com.verytouch.vkit.captcha.CaptchaContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ImageCaptchaProperties.class)
public class ImageCaptchaAutoConfiguration {

    @ConditionalOnBean(CaptchaCodeStore.class)
    @Bean
    public ImageCaptchaService imageCaptchaService(CaptchaCodeStore captchaCodeStore, ImageCaptchaProperties properties) {
        ImageCaptchaService imageCaptchaService = new ImageCaptchaService(captchaCodeStore, properties);
        CaptchaContext.addService(imageCaptchaService);
        return imageCaptchaService;
    }
}
