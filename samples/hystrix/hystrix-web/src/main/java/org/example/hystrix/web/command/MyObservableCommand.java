package org.example.hystrix.web.command;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import org.example.hystrix.api.ResultUtil;
import rx.Observable;

public class MyObservableCommand extends HystrixObservableCommand<ResultUtil> {

    public MyObservableCommand() {
        super(HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("my"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerEnabled(true)));
    }

    /*
     * 参考RxJava Observable ==> 观察者模式(发布/订阅模式)
     * 返回Observable对象，即被观察者(发布者)，可以一次发布多条消息
     */
    @Override
    protected Observable<ResultUtil> construct() {
        return Observable.from(new ResultUtil[]{ResultUtil.ok().msg("111"), ResultUtil.ok().msg("222")});
    }

    @Override
    protected Observable<ResultUtil> resumeWithFallback() {
        return Observable.just(ResultUtil.fail().msg("熔断"));
    }
}
