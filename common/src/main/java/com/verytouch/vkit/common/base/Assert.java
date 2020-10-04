package com.verytouch.vkit.common.base;

import lombok.Getter;

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
     * 断言失败抛出的异常
     */
    public static class AssertException extends RuntimeException {

        @Getter
        private final APICode apiCode;

        public AssertException(APICode apiCode, Throwable cause) {
            super(apiCode.getDesc(), cause);
            this.apiCode = apiCode;
        }

        public AssertException(APICode apiCode) {
            super(apiCode.getDesc());
            this.apiCode = apiCode;
        }

        public AssertException(String message, Throwable cause) {
            super(message, cause);
            this.apiCode = APICode.ERROR;
        }

        public AssertException(String message) {
            super(message);
            this.apiCode = APICode.ERROR;
        }

        public AssertException(Throwable cause) {
            super(cause);
            this.apiCode = APICode.ERROR;
        }
    }
}
