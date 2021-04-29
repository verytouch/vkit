package com.verytouch.vkit.captcha.tencent;

import com.verytouch.vkit.captcha.CaptchaParams;
import lombok.Data;

/**
 * 腾讯验证码参数
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Data
public class TencentCaptchaParams implements CaptchaParams {

    private String ticket;
    private String randStr;
    private String userIp;

}
