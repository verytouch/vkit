package org.example.hystrix.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * fallback：实现HelloFeign，重写对应方法作为fallback逻辑
 * fallbackFactory：实现FallbackFactory<HelloFeign>，可以获取异常信息
 */
// @FeignClient(value = "hystrix-service", fallbackFactory = HelloFeignFallbackFactory.class)
@FeignClient(value = "hystrix-service")
public interface HelloFeign {

    @GetMapping("hello")
    ResultUtil hello();
}
