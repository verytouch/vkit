package top.verytouch.vkit.rate;

import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.verytouch.vkit.common.exception.BusinessException;
import top.verytouch.vkit.common.util.DateUtils;
import top.verytouch.vkit.rbac.util.IPUtils;
import top.verytouch.vkit.rbac.util.Oauth2Utils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * rate service
 *
 * @author verytouch
 * @since 2023/10/14 12:07
 */
@RequiredArgsConstructor
public class RateService {

    private final RateCache cache;

    public boolean shoodLimit(RateLimiter limiter) {
        String key = this.getKey(limiter);
        if (cache.exist(key)) {
            return true;
        }
        cache.store(key, this.getExpireAt(limiter));
        return false;
    }

    private String getKey(RateLimiter limiter) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        switch (limiter.key()) {
            case PATH:
                return request.getRequestURI();
            case IP:
                return IPUtils.getClientIpByReq(request);
            case USER_ID:
                Oauth2Utils.getUsername();
        }
        throw new BusinessException("unmatched rate key " + limiter.key());
    }

    private long getExpireAt(RateLimiter limiter) {
        return LocalDateTime.now()
                .plus(limiter.time(), limiter.unit())
                .toInstant(DateUtils.ZONE_OFFSET)
                .toEpochMilli();
    }

}
