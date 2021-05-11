package com.verytouch.vkit.captcha.image;

import com.verytouch.vkit.common.util.RandomUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证码存到内存中，使用map实现
 * key没有上限
 * 过期策略为：store和remove时才清除过期key
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
public class InMemoryCaptchaCodeStore implements CaptchaCodeStore {

    private final Map<String, CodeValue> store = new HashMap<>();

    @Override
    public String store(String value, long expireSeconds) {
        String key = RandomUtils.uuid();
        synchronized (store) {
            clearExpiredKey();
            store.put(key, new CodeValue(value, LocalDateTime.now().plusSeconds(expireSeconds)));
            return key;
        }
    }

    @Override
    public String remove(String key) {
        synchronized (store) {
            clearExpiredKey();
            CodeValue codeVal = store.remove(key);
            if (codeVal == null || codeVal.isExpired()) {
                return null;
            }
            return codeVal.value;
        }
    }

    @Override
    public void clearExpiredKey() {
        store.entrySet().removeIf(next -> next.getValue().isExpired());
    }

    private static class CodeValue {
        String value;
        LocalDateTime expireAt;

        public CodeValue(String value, LocalDateTime expireAt) {
            this.value = value;
            this.expireAt = expireAt;
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expireAt);
        }
    }
}
