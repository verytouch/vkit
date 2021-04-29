package com.verytouch.vkit.captcha.image;

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
     * 验证码长度
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
     * 图片
     */
    private String suffix = "png";

    /**
     * 字体名称
     */
    private String fontName = "微软雅黑";

    /**
     * 字体样式
     */
    private int fontStyle = Font.BOLD;

    /**
     * 字体大小
     */
    private int fontSize = 40;

    /**
     * 基础字符
     */
    private String chars = "123456789abcdefghijkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";

    /**
     * 缓存过期时间，单位秒
     */
    private long expireSeconds = 600;
}
