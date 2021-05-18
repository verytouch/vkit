package top.verytouch.vkit.ocr.ali;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里OCR自动配置类
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Configuration
@EnableConfigurationProperties(AliOcrProperties.class)
@Slf4j
@ConditionalOnProperty(prefix = "vkit.ocr.ali", name = "enabled", havingValue = "true")
public class AliOcrAutoConfiguration {

    @Bean
    public AliOcrService aliOcrService(AliOcrProperties properties) {
        log.info("已启用AliOcr自动配置");
        return new AliOcrService(properties);
    }
}
