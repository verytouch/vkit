package com.verytouch.vkit.common.base;

import lombok.Getter;

/**
 * 断言工具类，失败抛出 {@link AssertException} 应由最外层统一处理
 *
 * @author verytouch
 * @since 2020/9/30 9:10
 */
public final class Assert {

    /**
     * 元素满足条件condition
     */
    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertException(message);
        }
    }

    /**
     * 元素满足条件condition
     */
    public static void require(boolean condition, APICode apiCode) {
        if (!condition) {
            throw new AssertException(apiCode);
        }
    }

    /**
     * 元素满足正则regex
     */
    public static void require(String target, String regex, String message) {
        require(Pattern.matches(regex, target), message);
    }

    /**
     * 元素非null
     */
    public static Object nonNull(Object target, String message) {
        require(target != null, message);
        return target;
    }

    /**
     * 元素非空
     * Object -> != null
     * Iterable -> hasNext()
     * Array -> length > 0
     * Map -> size() > 0
     * Object -> toString() != ""
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
     * 元素非空（包括trim后）
     */
    public static String nonBlank(String string, String message) {
        require(string != null, message);
        require(!"".equals(string), message);
        require(!"".equals(string.trim()), message);
        return string;
    }

    /**
     * 元素长度处于区间[min, max]
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
