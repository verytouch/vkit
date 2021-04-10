package com.verytouch.vkit.captcha.tencent;

import com.verytouch.vkit.captcha.CaptchaParams;
import lombok.Data;

@Data
public class TencentCaptchaParams implements CaptchaParams {

    private String ticket;
    private String randStr;
    private String userIp;

}
