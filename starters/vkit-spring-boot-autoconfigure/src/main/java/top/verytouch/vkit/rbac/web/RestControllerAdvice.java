package top.verytouch.vkit.rbac.web;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
        return Response.error(ApiCode.PARAM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Response<String> businessException(Exception e) {
        return Response.error(ApiCode.PARAM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Response<String> nullPointerException(Exception e) {
        return logError(ApiCode.PARAM_ABSENT, e);
    }

    /**
     * 认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public Response<String> authenticationException() {
        return error(ApiCode.SYS_UNAUTHENTICATED);
    }

    /**
     * 权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Response<String> accessDeniedException() {
        return error(ApiCode.SYS_FORBIDDEN);
    }

    /**
     * 404异常，仅存在以下配置时生效
     * spring.mvc.throw-exception-if-no-handler-found=true
     * spring.resources.add-mappings=false
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Response<String> noHandlerFoundException() {
        return error(ApiCode.SYS_NO_HANDLER);
    }

    /**
     * 其他异常
     */
    @ExceptionHandler({Exception.class})
    public Response<String> exception(Exception e) {
        return logError(ApiCode.ERROR, e);
    }

    private Response<String> logError(ApiCode code, Exception e) {
        log.error(code.getDesc(), e);
        return Response.error(code.getCode(), code.getDesc()).setTraceId(MDC.get(MDCInterceptor.traceId));
    }

    private Response<String> error(ApiCode code) {
        return Response.error(code.getCode(), code.getDesc()).setTraceId(MDC.get(MDCInterceptor.traceId));
    }
}
