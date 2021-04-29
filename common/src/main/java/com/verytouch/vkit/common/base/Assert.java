package com.verytouch.vkit.common.base;

import com.verytouch.vkit.common.exception.AssertException;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 断言工具类，失败抛出 {@link AssertException} 应由最外层统一处理
 *
 * @author verytouch
 * @since 2020/9/30 9:10
 */
public final class Assert {

    /**
     * 满足条件condition
     *
     * @param condition 条件
     * @param message 错误信息
     */
    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertException(message);
        }
    }

    /**
     * 满足条件condition
     *
     * @param condition 条件
     * @param apiCode 错误码
     */
    public static void require(boolean condition, APICode apiCode) {
        if (!condition) {
            throw new AssertException(apiCode);
        }
    }

    /**
     * 满足正则regex
     *
     * @param target 校验对象
     * @param regex 正则
     * @param message 错误信息
     * @return target
     */
    public static String require(String target, String regex, String message) {
        require(Pattern.matches(regex, target), message);
        return target;
    }

    /**
     * 非null
     *
     * @param target 校验对象
     * @param message 错误信息
     * @return target
     */
    public static Object nonNull(Object target, String message) {
        require(target != null, message);
        return target;
    }

    /**
     * 非空
     * <ol>
     * <li>Object: != null</li>
     * <li>Iterable: hasNext()</li>
     * <li>Array: length > 0</li>
     * <li>Map: size() > 0</li>
     * <li>Object: toString() != ""</li>
     * </ol>
     *
     * @param target 校验对象
     * @param message 错误信息
     * @return target
     */
    public static Object nonEmpty(Object target, String message) {
        require(target != null, message);
        if (target instanceof Iterable) {
            require(((Iterable<?>)target).iterator().hasNext(), message);
        }
        if (target instanceof Object[]) {
            require(((Object[])target).length > 0, message);
        }
        if (target instanceof Map) {
            require(((Map)target).size() > 0, message);
        }
        require(!"".equals(target.toString()), message);
        return target;
    }

    /**
     * 非空（包括trim后）
     *
     * @param string 校验对象
     * @param message 错误信息
     * @return string
     */
    public static String nonBlank(String string, String message) {
        require(string != null, message);
        require(!"".equals(string), message);
        require(!"".equals(string.trim()), message);
        return string;
    }

    /**
     * 长度处于区间[min, max]
     *
     * @param string 校验对象
     * @param min 最小长度
     * @param max 最大长度
     * @param message 错误信息
     * @return string
     */
    public static String length(String string, int min, int max, String message) {
        require(string != null, message);
        require(min > 0 && max > 0, message);
        require(min <= max, message);
        require(min <= string.length(), message);
        require(max >= string.length(), message);
        return string;
    }

    /**
     * 对象equals且非null
     *
     * @param o1 对象1
     * @param o2 对象2
     * @param message 错误信息
     */
    public static void equals(Object o1, Object o2, String message) {
        require(o1 != null && o1.equals(o2), message);
    }

    /**
     * 字符串equals且非null，忽略大小写
     *
     * @param s1 字符串1
     * @param s2 字符串2
     * @param message 错误信息
     */
    public static void equalsIgnoreCase(String s1, String s2, String message) {
        require(s1 != null && s1.equalsIgnoreCase(s2), message);
    }

    /**
     * 类型转换
     *
     * @param target 目标对象
     * @param clazz 目标Class
     * @param message 错误信息
     * @param <T> 目标类型
     */
    public static <T> T instanceOf(Object target, Class<T> clazz, String message) {
        require(target != null && clazz.isAssignableFrom(target.getClass()), message);
        return (T) target;
    }
}
