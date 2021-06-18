package top.verytouch.vkit.common.util;

import top.verytouch.vkit.common.exception.BusinessException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * 随机工具类
 *
 * @author verytouch
 * @since 2021/3/7 20:41
 */
@SuppressWarnings("unused")
public class RandomUtils {

    public static final String BASE_CHAR_CODE = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
    public static final String BASE_CHAR_NUM = "1234567890";

    /**
     * uuid不带杠
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 从base中随机生成length位字符
     */
    public static String character(String base, int length) {
        Objects.requireNonNull(base, "base can not be null");
        StringBuilder stringBuffer = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            stringBuffer.append(base.charAt(random.nextInt(base.length())));
        }
        return stringBuffer.toString();
    }

    /**
     * 随机生成length位数字
     */
    public static String number(int length) {
        return character(BASE_CHAR_NUM, length);
    }

    /**
     * 随机生成4位图片验证码
     *
     * @param os 输出流
     * @return 验证码
     */
    public static String captcha(OutputStream os) {
        BufferedImage image = new BufferedImage(200, 70, BufferedImage.TYPE_INT_RGB);
        String str = captcha(200, 70, image, BASE_CHAR_CODE, 4);
        try {
            ImageIO.write(image, "png", os);
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new BusinessException("生成验证码失败：" + e.getMessage());
        }
        return str;
    }

    /**
     * 随机生成图片验证码
     */
    public static String captcha(int width, int height, BufferedImage img, final String base, int length) {
        Graphics2D graphics = (Graphics2D) img.getGraphics();
        //设置画笔颜色-验证码背景色
        graphics.setColor(Color.WHITE);
        //填充背景
        graphics.fillRect(0, 0, width, height);
        graphics.setFont(new Font("微软雅黑", Font.BOLD, 40));
        //数字和字母的组合
        StringBuilder sb = new StringBuilder();
        int x = 10;  //旋转原点的 x 坐标
        String ch;
        Random random = new Random();
        char[] chars = character(base, length).toCharArray();
        for (char aChar : chars) {
            graphics.setColor(color());
            //设置字体旋转角度
            int degree = random.nextInt() % 30;  //角度小于30度
            ch = String.valueOf(aChar);
            sb.append(ch);
            //正向旋转
            graphics.rotate(degree * Math.PI / 180, x, 45);
            graphics.drawString(ch, x, 45);
            //反向旋转
            graphics.rotate(-degree * Math.PI / 180, x, 45);
            x += 48;
        }

        //画干扰线
        for (int i = 0; i < 6; i++) {
            // 设置随机颜色
            graphics.setColor(color());
            // 随机画线
            graphics.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
        }

        //添加噪点
        for (int i = 0; i < 30; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            graphics.setColor(color());
            graphics.fillRect(x1, y1, 2, 2);
        }
        return sb.toString();

    }

    /**
     * 随机取色
     */
    public static Color color() {
        Random ran = new Random();
        return new Color(ran.nextInt(256), ran.nextInt(256), ran.nextInt(256));
    }

}
