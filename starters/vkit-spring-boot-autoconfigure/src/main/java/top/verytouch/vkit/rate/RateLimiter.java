package top.verytouch.vkit.rate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * rate limiter
 *
 * @author verytouch
 * @since 2023/10/14 11:56
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    RateKey key() default RateKey.PATH;

    long expire();

    ChronoUnit timUnit() default ChronoUnit.MINUTES;
}
