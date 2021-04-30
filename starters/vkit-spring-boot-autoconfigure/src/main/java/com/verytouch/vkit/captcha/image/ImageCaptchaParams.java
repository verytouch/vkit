package com.verytouch.vkit.captcha.image;

import com.verytouch.vkit.captcha.CaptchaParams;
import lombok.Data;

/**
 * 图形验证码参数
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Data
public class ImageCaptchaParams implements CaptchaParams {

    private String key;
    private String value;

}
