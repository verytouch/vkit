package top.verytouch.vkit.captcha.image;

import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

public class RedisCaptchaCodeStore implements CaptchaCodeStore {

    private final RedisTemplate redisTemplate;

    public RedisCaptchaCodeStore(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String store(String value, long seconds) {
        String key = generateKey();
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(seconds));
        return key;
    }

    @Override
    public String remove(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        redisTemplate.delete(key);
        return value.toString();
    }
}
