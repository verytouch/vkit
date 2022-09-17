package org.springframework.cloud.openfeign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FeignHystrixExtensionAutoConfiguration {

    @Bean
    @Primary
    public Targeter targeter() {
        return new GlobalFailFastHystrixTargeter();
    }

    @Bean
    @ConditionalOnProperty(name = "hystrix.extension.requestAttributeStrategy", havingValue = "true")
    public RequestAttributeHystrixConcurrencyStrategy hystrixConcurrencyStrategy() {
        return new RequestAttributeHystrixConcurrencyStrategy();
    }
}
