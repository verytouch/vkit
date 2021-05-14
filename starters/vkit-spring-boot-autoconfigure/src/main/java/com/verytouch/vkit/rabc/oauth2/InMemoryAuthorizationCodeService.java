package com.verytouch.vkit.rabc.oauth2;

import com.verytouch.vkit.common.base.Cache;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

import java.time.Duration;

/**
 * 自定义授权码存储
 *
 * @author verytouch
 * @since 2021/5/14 17:34
 */
public class InMemoryAuthorizationCodeService extends RandomValueAuthorizationCodeServices {

    private final Cache<OAuth2Authentication> cache;
    private final Duration duration;

    public InMemoryAuthorizationCodeService(Duration duration) {
        this.cache = new Cache<>();
        this.duration = duration;
    }

    @Override
    protected void store(String code, OAuth2Authentication oAuth2Authentication) {
        cache.put(code, oAuth2Authentication, duration);
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        return cache.remove(code);
    }
}
