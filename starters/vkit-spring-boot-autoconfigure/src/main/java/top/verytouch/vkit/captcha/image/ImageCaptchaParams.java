package top.verytouch.vkit.captcha.image;

import top.verytouch.vkit.captcha.CaptchaParams;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 图形验证码参数
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Data
@Accessors(chain = true)
public class ImageCaptchaParams implements CaptchaParams {

    private String key;
    private String value;

}
