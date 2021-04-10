package com.verytouch.vkit.captcha.image;

import com.verytouch.vkit.captcha.CaptchaParams;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ImageCaptchaParams implements CaptchaParams {

    private String key;
    private String value;

}
