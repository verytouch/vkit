package top.verytouch.vkit.oss.minio;

import top.verytouch.vkit.oss.OssProperties;
import top.verytouch.vkit.oss.OssService;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIo自动配置类
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnClass(MinioClient.class)
@Slf4j
public class MinIoAutoConfiguration {

    @Bean
    public OssService minioService(OssProperties properties) {
        log.info("已启用MinIo自动配置");
        return new MinIoService(properties);
    }
}
