package top.verytouch.vkit.captcha;

import java.util.Map;

/**
 * 验证码接口
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
public interface CaptchaService {

    /**
     * 获取验证码类型
     */
    CaptchaEnum getType();

    /**
     * 校验验证码
     *
     * @param params 验证码参数
     */
    void verify(CaptchaParams params);

    /**
     * 校验验证码
     *
     * @param params 验证码参数
     */
    void verify(Map<String, String> params);
}
