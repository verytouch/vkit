package com.example.oauth2.server.oauth2;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

import java.time.Duration;

public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

    private final String CODE_PREFIX = "auth:code:";
    private final Duration CODE_TIMEOUT = Duration.ofMinutes(3);
    private final RedisTemplate<String, OAuth2Authentication> redisTemplate;

    public RedisAuthorizationCodeServices(RedisConnectionFactory redisConnectionFactory) {
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.afterPropertiesSet();
    }

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        redisTemplate.opsForValue().set(CODE_PREFIX + code, authentication, CODE_TIMEOUT);
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        OAuth2Authentication oAuth2Authentication = redisTemplate.opsForValue().get(CODE_PREFIX + code);
        redisTemplate.delete(CODE_PREFIX + code);
        return oAuth2Authentication;
    }
}
