package top.verytouch.vkit.captcha.image;

import top.verytouch.vkit.common.base.Cache;
import top.verytouch.vkit.common.util.RandomUtils;

import java.time.Duration;

/**
 * 验证码存到内存中
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@SuppressWarnings("unused")
public class InMemoryCaptchaCodeStore implements CaptchaCodeStore {

    private final Cache<String> cache;

    public InMemoryCaptchaCodeStore() {
        this.cache = new Cache<>();
    }

    @Override
    public String store(String value, long expireSeconds) {
        String key = RandomUtils.uuid();
        cache.put(key, value, Duration.ofSeconds(expireSeconds));
        return key;
    }

    @Override
    public String remove(String key) {
        return cache.remove(key);
    }
}
