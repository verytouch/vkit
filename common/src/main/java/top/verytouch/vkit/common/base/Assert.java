package top.verytouch.vkit.common.base;

import top.verytouch.vkit.common.exception.AssertException;
import top.verytouch.vkit.common.util.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
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
     * @param message   错误信息
     */
    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertException(message);
        }
    }

    /**
     * 满足条件predicate
     *
     * @param target    校验对象
     * @param predicate 条件
     * @param message   错误信息
     * @param <T>       对象类型
     */
    public static <T> void require(T target, Predicate<T> predicate, String message) {
        require(predicate.test(target), message);
    }

    /**
     * 满足条件condition
     *
     * @param condition 条件
     * @param apiCode   错误码
     */
    public static void require(boolean condition, ApiCode apiCode) {
        if (!condition) {
            throw new AssertException(apiCode);
        }
    }

    /**
     * 满足正则regex
     *
     * @param target  校验对象
     * @param regex   正则
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
     * @param target  校验对象
     * @param message 错误信息
     */
    public static void nonNull(Object target, String message) {
        require(target != null, message);
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
     * @param target  校验对象
     * @param message 错误信息
     */
    public static void nonEmpty(Object target, String message) {
        require(target != null, message);
        if (target instanceof Iterable) {
            require(((Iterable<?>) target).iterator().hasNext(), message);
        } else if (target instanceof Object[]) {
            require(((Object[]) target).length > 0, message);
        } else if (target instanceof Map) {
            require(((Map) target).size() > 0, message);
        } else {
            require(target.toString().length() > 0, message);
        }
    }

    /**
     * 非空（包括trim后）
     *
     * @param string  校验对象
     * @param message 错误信息
     */
    public static void nonBlank(String string, String message) {
        require(string, StringUtils::isNotBlank, message);
    }

    /**
     * 非空（包括trim后）
     *
     * @param string  校验对象
     * @param message 错误信息
     * @return string.trim()
     */
    public static String trimmedNonBlank(String string, String message) {
        require(string, StringUtils::isNotBlank, message);
        return string.trim();
    }

    /**
     * 长度处于区间[min, max]
     *
     * @param string  校验对象
     * @param min     最小长度
     * @param max     最大长度
     * @param message 错误信息
     */
    public static void length(String string, int min, int max, String message) {
        int len = StringUtils.length(string);
        require(len >= min && len <= max, message);
    }

    /**
     * target在objs中
     *
     * @param target  校验对象
     * @param message 错误信息
     * @param objs    候选对象
     */
    public static void oneOf(Object target, String message, Object... objs) {
        for (Object obj : objs) {
            if (Objects.equals(target, obj)) {
                return;
            }
        }
        throw new AssertException(message);
    }

    /**
     * 对象equals且非null
     *
     * @param o1      对象1
     * @param o2      对象2
     * @param message 错误信息
     */
    public static void equals(Object o1, Object o2, String message) {
        require(o1 != null && o1.equals(o2), message);
    }

    /**
     * 字符串equals且非null，忽略大小写
     *
     * @param s1      字符串1
     * @param s2      字符串2
     * @param message 错误信息
     */
    public static void equalsIgnoreCase(String s1, String s2, String message) {
        require(s1 != null && s1.equalsIgnoreCase(s2), message);
    }

    /**
     * 类型转换
     *
     * @param target  目标对象
     * @param clazz   目标Class
     * @param message 错误信息
     * @param <T>     目标类型
     */
    public static <T> T instanceOf(Object target, Class<T> clazz, String message) {
        require(target != null && clazz.isAssignableFrom(target.getClass()), message);
        return (T) target;
    }
}
