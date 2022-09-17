package org.example.hystrix.api;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class HelloFeignFallbackFactory implements FallbackFactory<HelloFeign> {

    @Override
    public HelloFeign create(Throwable cause) {
        return new HelloFeign() {
            @Override
            public ResultUtil hello() {
                return ResultUtil.fail().msg("熔断：" + cause.getMessage());
            }
        };
    }
}
