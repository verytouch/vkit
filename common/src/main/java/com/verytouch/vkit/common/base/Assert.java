package com.verytouch.vkit.common.base;

import lombok.Getter;

/**
 * 断言工具类
 *
 * @author verytouch
 * @since 2020/9/30 9:10
 */
public final class Assert {

    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertException(message);
        }
    }

    public static void require(boolean condition, APICode apiCode) {
        if (!condition) {
            throw new AssertException(apiCode);
        }
    }

    public static void nonNull(Object target, String message) {
        require(target != null, message);
    }

    /**
     * 断言失败抛出的异常，应由最外层统一处理
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
