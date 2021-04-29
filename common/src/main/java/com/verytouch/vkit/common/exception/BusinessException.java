package com.verytouch.vkit.common.exception;

/**
 * 业务异常
 *
 * @author verytouch
 * @since 2021/3/7 21:18
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
