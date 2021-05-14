package com.verytouch.vkit.rabc.web;

import com.verytouch.vkit.common.base.ApiCode;
import com.verytouch.vkit.common.base.Response;
import com.verytouch.vkit.common.exception.AssertException;
import com.verytouch.vkit.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * SpringMvc异常处理器
 *
 * @author verytouch
 * @since 2021/5/13 15:13
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class RestControllerAdvice {

    @ExceptionHandler(AssertException.class)
    public Response<String> assertException(Exception e) {
        return error(ApiCode.PARAM_ERROR, e, false);
    }

    @ExceptionHandler(BusinessException.class)
    public Response<String> businessException(Exception e) {
        return error(ApiCode.PARAM_ERROR, e, false);
    }

    @ExceptionHandler(NullPointerException.class)
    public Response<String> NPEException(Exception e) {
        return error(ApiCode.PARAM_ABSENT, e, true);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Response<String> accessDeniedException(AccessDeniedException e) {
        return error(ApiCode.SYS_UNAUTHORIZED);
    }

    @ExceptionHandler({Exception.class})
    public Response<String> exception(Exception e) {
        return error(ApiCode.ERROR, e, true);
    }

    private Response<String> error(ApiCode code, Exception e, boolean printLog) {
        if (printLog) {
            log.error(code.getDesc(), e);
        }
        String msg = e instanceof AssertException ? e.getMessage() : code.getDesc();
        return Response.error(code.getCode(), msg);
    }

    private Response<String> error(ApiCode code) {
        return Response.error(code.getCode(), code.getDesc());
    }
}
