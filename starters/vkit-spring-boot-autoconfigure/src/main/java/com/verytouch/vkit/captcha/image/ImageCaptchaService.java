package com.verytouch.vkit.captcha.image;

import com.verytouch.vkit.captcha.CaptchaEnum;
import com.verytouch.vkit.captcha.CaptchaParams;
import com.verytouch.vkit.captcha.CaptchaService;
import com.verytouch.vkit.common.base.Assert;
import com.verytouch.vkit.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Map;
import java.util.Random;

/**
 * 简单图形验证码
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Slf4j
public class ImageCaptchaService implements CaptchaService {

    private final CaptchaCodeStore codeStore;

    public final ImageCaptchaProperties properties;

    public ImageCaptchaService(CaptchaCodeStore codeStore, ImageCaptchaProperties properties) {
        this.codeStore = codeStore;
        this.properties = properties;
    }

    @Override
    public CaptchaEnum getType() {
        return CaptchaEnum.IMAGE;
    }

    @Override
    public void verify(CaptchaParams params) {
        ImageCaptchaParams p = Assert.instanceOf(params, ImageCaptchaParams.class, "参数类型错误，必须为ImageCaptchaParams");
        Assert.nonBlank(p.getKey(), "请先获取验证码");
        Assert.nonBlank(p.getValue(), "请输入" + properties.getLength() + "位验证码");
        Assert.equalsIgnoreCase(p.getValue(), codeStore.remove(p.getKey()), "验证码错误");
    }

    @Override
    public void verify(Map<String, String> params) {
        ImageCaptchaParams imageCaptchaParams = new ImageCaptchaParams();
        imageCaptchaParams.setKey(params.get("key"));
        imageCaptchaParams.setValue(params.get("value"));
        verify(imageCaptchaParams);
    }

    /**
     * 生成并缓存验证码
     *
     * @param outputStream 输出流
     * @return 验证码key
     */
    public String drawAndStore(OutputStream outputStream) {
        String value = draw(outputStream);
        return codeStore.store(value, properties.getDefaultExpireSeconds());
    }

    /**
     * 生成并缓存验证码
     *
     * @param outputStream  输出流
     * @param expireSeconds 过期时间，单位秒
     * @return 验证码key
     */
    public String drawAndStore(OutputStream outputStream, long expireSeconds) {
        String value = draw(outputStream);
        return codeStore.store(value, expireSeconds);
    }

    /**
     * 生成验证码
     *
     * @param outputStream 输出流
     * @return 验证码value
     */
    public String draw(OutputStream outputStream) {
        Random random = new Random();
        char[] code = randomChar(random);
        BufferedImage image = new BufferedImage(properties.getWidth(), properties.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        // 设置画笔颜色-验证码背景色
        graphics.setColor(Color.WHITE);
        //填充背景
        graphics.fillRect(0, 0, properties.getWidth(), properties.getHeight());
        graphics.setFont(new Font(properties.getFontName(), properties.getFontStyle(), properties.getFontSize()));

        // 画验证码，x为旋转原点的横坐标
        int x = 10;
        for (char c : code) {
            graphics.setColor(randomColor(random));
            //设置字体旋转角度，小于30度
            int degree = random.nextInt() % 30;
            //正向旋转
            graphics.rotate(degree * Math.PI / 180, x, 45);
            graphics.drawString(String.valueOf(c), x, 45);
            //反向旋转
            graphics.rotate(-degree * Math.PI / 180, x, 45);
            x += 48;
        }
        // 画干扰线
        for (int i = 0; i < 6; i++) {
            // 设置随机颜色
            graphics.setColor(randomColor(random));
            // 随机画线
            graphics.drawLine(random.nextInt(properties.getWidth()),
                    random.nextInt(properties.getHeight()),
                    random.nextInt(properties.getWidth()),
                    random.nextInt(properties.getHeight()));
        }
        // 添加噪点
        for (int i = 0; i < 30; i++) {
            int x1 = random.nextInt(properties.getWidth());
            int y1 = random.nextInt(properties.getHeight());
            graphics.setColor(randomColor(random));
            graphics.fillRect(x1, y1, 2, 2);
        }
        // 写到输出流
        try {
            ImageIO.write(image, properties.getSuffix(), outputStream);
        } catch (Exception e) {
            log.error("生成图片验证码失败", e);
            throw new BusinessException("生成图片验证码失败");
        }
        return new String(code);
    }

    /**
     * 随机取色
     */
    private Color randomColor(Random random) {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    /**
     * 生成验证码
     */
    private char[] randomChar(Random random) {
        char[] code = new char[properties.getLength()];
        String base = properties.getChars();
        for (int i = 0; i < code.length; i++) {
            code[i] = base.charAt(random.nextInt(base.length()));
        }
        return code;
    }
}
