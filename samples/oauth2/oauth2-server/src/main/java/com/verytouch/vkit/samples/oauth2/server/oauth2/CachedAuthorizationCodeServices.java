package com.verytouch.vkit.samples.oauth2.server.oauth2;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

import java.time.Duration;

public class CachedAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

    private static final String CODE_KEY = "auth:code:%s";
    private static final Duration CODE_TIMEOUT = Duration.ofMinutes(3);
    private final Cache<String, OAuth2Authentication> cache;

    public CachedAuthorizationCodeServices() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();
        cache = cacheManager.createCache("authorizationCode",
            CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, OAuth2Authentication.class, ResourcePoolsBuilder.heap(1))
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(CODE_TIMEOUT)));

    }

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        cache.put(String.format(CODE_KEY, code), authentication);
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        OAuth2Authentication authentication = cache.get(String.format(CODE_KEY, code));
        if (authentication != null) {
            cache.remove(String.format(CODE_KEY, code));
        }
        return authentication;
    }
}
