package top.verytouch.vkit.captcha.image;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.awt.*;

/**
 * 图形验证码配置类
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Data
@Validated
@ConfigurationProperties(prefix = "vkit.captcha.image")
public class ImageCaptchaProperties {

    /**
     * 验证码字符长度
     */
    private int length = 4;

    /**
     * 图片宽度，根据验证码长度自行调整
     */
    private int width = 200;

    /**
     * 图片高度
     */
    private int height = 70;

    /**
     * 图片后缀
     */
    private String suffix = "png";

    /**
     * 字体名称
     */
    private String fontName = "微软雅黑";

    /**
     * 字体大小
     */
    private int fontSize = 40;

    /**
     * 字体最大旋转角度
     */
    private int fontRotate = 60;

    /**
     * 基础字符
     */
    @SuppressWarnings("all")
    private String chars = "123456789abcdefghijkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";

    /**
     * 干扰线条数
     */
    private int noisyLines = 15;

    /**
     * 干扰点个数
     */
    private int noisyPoints = 30;

    /**
     * 缓存默认过期时间，单位秒
     */
    private long defaultExpireSeconds = 600;
}
