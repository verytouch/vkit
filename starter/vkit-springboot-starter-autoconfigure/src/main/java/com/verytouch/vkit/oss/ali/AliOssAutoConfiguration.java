package com.verytouch.vkit.oss.ali;

import com.aliyun.oss.OSS;
import com.verytouch.vkit.oss.OssProperties;
import com.verytouch.vkit.oss.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里OSS自动配置类
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnClass(OSS.class)
@Slf4j
public class AliOssAutoConfiguration {

    @Bean
    public OssService aliOssService(OssProperties properties) {
        log.info("已启用AliOss自动配置");
        return new AliOssService(properties);
    }
}
