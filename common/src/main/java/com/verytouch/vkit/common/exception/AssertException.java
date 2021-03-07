package com.verytouch.vkit.common.exception;

import com.verytouch.vkit.common.base.APICode;
import lombok.Getter;

/**
 * 断言失败抛出的异常
 */
public class AssertException extends BusinessException {

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
