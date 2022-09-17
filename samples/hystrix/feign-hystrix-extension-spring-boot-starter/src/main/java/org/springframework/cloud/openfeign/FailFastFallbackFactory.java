package org.springframework.cloud.openfeign;

import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

public class FailFastFallbackFactory<T> implements FallbackFactory<T> {

    private static final Logger logger = LoggerFactory.getLogger(FallbackFactory.class);

    private final Class<T> clazz;

    public FailFastFallbackFactory(Class<T> target) {
        this.clazz = target;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T create(Throwable throwable) {
        return (T) Proxy.newProxyInstance(FallbackFactory.class.getClassLoader(), new Class[]{ clazz }, (proxy, method, args) -> {
            logger.error("feign服务异常，执行快速降级 =====> {}.{}", method.getDeclaringClass().getName(), method.getName());
            throw throwable;
        });
    }
}
