package top.verytouch.vkit.captcha.tencent;

import top.verytouch.vkit.captcha.CaptchaParams;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 腾讯验证码参数
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Data
@Accessors(chain = true)
public class TencentCaptchaParams implements CaptchaParams {

    private String ticket;
    private String randStr;
    private String userIp;

}
