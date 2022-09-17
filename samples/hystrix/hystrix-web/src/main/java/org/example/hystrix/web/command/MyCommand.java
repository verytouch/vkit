package org.example.hystrix.web.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.example.hystrix.api.ResultUtil;

public class MyCommand extends HystrixCommand<ResultUtil> {

    public MyCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("my"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerEnabled(true)));
    }

    @Override
    protected ResultUtil run() throws Exception {
        // 抛出 HystrixBadRequestException不会走fallback
        return ResultUtil.ok();
    }

    @Override
    protected ResultUtil getFallback() {
        return ResultUtil.fail().msg("熔断了");
    }
}
