package com.verytouch.vkit.captcha.image;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.awt.*;

@Data
@Validated
@ConfigurationProperties(prefix = "captcha.image")
public class ImageCaptchaProperties {
    /**
     * 图片宽度
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
     * 验证码长度
     */
    private int length = 4;
}
