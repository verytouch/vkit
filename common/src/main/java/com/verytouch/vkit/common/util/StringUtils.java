package com.verytouch.vkit.common.util;

import com.verytouch.vkit.common.base.Assert;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 字符串工具类
 *
 * @author verytouch
 * @since 2021/3/7 16:32
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 邮箱校验
     *
     * @param mail 邮箱
     * @return 格式是否正确
     */
    public static boolean isMail(String mail) {
        return Pattern.matches(".+@.+\\..+", mail);
    }

    /**
     * 国内手机号校验
     *
     * @param tel 手机号
     * @return 格式是否正确
     */
    public static boolean isTel(String tel) {
        return Pattern.matches("^1\\d{10}$", tel);
    }

    /**
     * 身份证号码校验
     *
     * @param idNo 身份证号码
     * @return 格式是否正确
     */
    public static boolean isIdNo(String idNo) {
        return Pattern.matches("^\\d{6}\\d{4}[01]\\d[0123]\\d\\d{3}[\\dXx]$", idNo);
    }

    /**
     * 从身份证号码获取性别
     *
     * @param idNo 身份证号码
     * @return 男：M，女：F
     */
    public static String genderFromIdNo(String idNo) {
        Assert.require(isIdNo(idNo), "身份证号码格式不正确");
        int remainder = Integer.parseInt(idNo.substring(16, 17)) % 2;
        return remainder == 1 ? "M" : "F";
    }

    /**
     * 从身份证号码获取生日
     *
     * @param idNo 身份证号码
     * @return yyyy-MM-dd
     */
    public static String birthdayFromIdNo(String idNo) {
        Assert.require(isIdNo(idNo), "身份证号码格式不正确");
        String year = idNo.substring(6, 10);
        String month = idNo.substring(10, 12);
        String day = idNo.substring(12, 14);
        return String.format("%s-%s-%s", year, month, day);
    }

    /**
     * 功能描述: 驼峰转下划线
     *
     * @param camel 驼峰字符串
     * @return 下划线字符串
     */
    public static String camelToUnderScore(String camel) {
        return Optional.ofNullable(camel).orElse("").replaceAll("([A-Z])", "_$1").toLowerCase();
    }

    /**
     * 功能描述: 下划线转驼峰
     *
     * @param underScore 下划线字符串
     * @return 驼峰字符串
     */
    public static String underScoreToCamel(String underScore) {
        String[] array = Optional.ofNullable(underScore).orElse("").split("_");
        return array[0] + Stream.of(array)
                .skip(1)
                .map(v -> isEmpty(v) ? "" : Character.toUpperCase(v.charAt(0)) + v.substring(1))
                .reduce((a, b) -> a + b)
                .orElse("");
    }

}
