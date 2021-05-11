package com.verytouch.vkit.common.exception;

import com.verytouch.vkit.common.base.ApiCode;
import lombok.Getter;

/**
 * 断言失败抛出的异常
 *
 * @author verytouch
 * @since 2021/3/7 21:18
 */
public class AssertException extends BusinessException {

    @Getter
    private final ApiCode apiCode;

    public AssertException(ApiCode apiCode, Throwable cause) {
        super(apiCode.getDesc(), cause);
        this.apiCode = apiCode;
    }

    public AssertException(ApiCode apiCode) {
        super(apiCode.getDesc());
        this.apiCode = apiCode;
    }

    public AssertException(String message, Throwable cause) {
        super(message, cause);
        this.apiCode = ApiCode.ERROR;
    }

    public AssertException(String message) {
        super(message);
        this.apiCode = ApiCode.ERROR;
    }

    public AssertException(Throwable cause) {
        super(cause);
        this.apiCode = ApiCode.ERROR;
    }
}
