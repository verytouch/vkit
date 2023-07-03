package top.verytouch.vkit.pay.ali;

import com.alipay.api.AlipayClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝自动配置类
 *
 * @author verytouch
 * @since 2021/4/30 10:32
 */
@Configuration
@EnableConfigurationProperties(AlipayProperties.class)
@ConditionalOnClass(AlipayClient.class)
@Slf4j
public class AlipayAutoConfiguration {

    @Bean
    public AlipayService alipayService(AlipayProperties alipayProperties) {
        log.info("已启用支付宝自动配置");
        return new AlipayService(alipayProperties);
    }
}
