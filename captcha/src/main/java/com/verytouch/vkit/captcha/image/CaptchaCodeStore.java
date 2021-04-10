package com.verytouch.vkit.captcha.image;

public interface CaptchaCodeStore {

    /**
     * 存储验证码
     *
     * @param value 验证码value
     * @return 验证码key
     */
    String store(String value);

    /**
     * 获取验证码
     *
     * @param key 验证码key
     * @return 验证码value，取不到返回null
     */
    String remove(String key);
}
