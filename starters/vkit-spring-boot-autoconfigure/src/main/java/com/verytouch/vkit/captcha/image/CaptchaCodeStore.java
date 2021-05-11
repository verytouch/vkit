package com.verytouch.vkit.captcha.image;

/**
 * 验证码存储
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
public interface CaptchaCodeStore {

    /**
     * 存储验证码
     *
     * @param value   验证码value
     * @param seconds 过期时间，单位秒
     * @return 验证码key
     */
    String store(String value, long seconds);

    /**
     * 获取验证码
     *
     * @param key 验证码key
     * @return 验证码value，取不到返回null
     */
    String remove(String key);

    /**
     * 清除过期的验证码
     */
    void clearExpiredKey();
}
