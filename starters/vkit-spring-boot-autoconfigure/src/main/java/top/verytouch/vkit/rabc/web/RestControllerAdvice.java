package top.verytouch.vkit.rabc.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;
import top.verytouch.vkit.common.base.ApiCode;
import top.verytouch.vkit.common.base.Response;
import top.verytouch.vkit.common.exception.AssertException;
import top.verytouch.vkit.common.exception.BusinessException;

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

    /**
     * 断言异常
     */
    @ExceptionHandler(AssertException.class)
    public Response<String> assertException(Exception e) {
        return error(ApiCode.PARAM_ERROR, e, false);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Response<String> businessException(Exception e) {
        return error(ApiCode.PARAM_ERROR, e, false);
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Response<String> nullPointerException(Exception e) {
        return error(ApiCode.PARAM_ABSENT, e, true);
    }

    /**
     * 认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public Response<String> authenticationException(AuthenticationException e) {
        return error(ApiCode.SYS_UNAUTHENTICATED);
    }

    /**
     * 权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Response<String> accessDeniedException(AccessDeniedException e) {
        return error(ApiCode.SYS_FORBIDDEN);
    }

    /**
     * 404异常，仅存在以下配置时生效
     * spring.mvc.throw-exception-if-no-handler-found=true
     * spring.resources.add-mappings=false
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Response<String> noHandlerFoundException(NoHandlerFoundException e) {
        return error(ApiCode.SYS_NO_HANDLER);
    }

    /**
     * 其他异常
     */
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
