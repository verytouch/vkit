package top.verytouch.vkit.rbac.web;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.multipart.MultipartFile;
import top.verytouch.vkit.common.util.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Controller日志打印
 *
 * @author verytouch
 * @since 2023/9/9 15:42
 */
@Aspect
@Slf4j
public class RestControllerLogger {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void webLog() {
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        if (log.isInfoEnabled()) {
            String args = Arrays.stream(joinPoint.getArgs())
                    .filter(obj -> !(obj instanceof MultipartFile))
                    .filter(obj -> !(obj instanceof HttpServletRequest))
                    .filter(obj -> !(obj instanceof HttpServletResponse))
                    .map(JsonUtils::toJson)
                    .collect(Collectors.joining(", "));
            log.info("request ===> {}.{}: {}", joinPoint.getTarget().getClass().getCanonicalName(), joinPoint.getSignature().getName(), args);
        }
    }

    @AfterReturning(returning = "response", pointcut = "webLog()")
    public void doAfterReturning(Object response) {
        if (log.isInfoEnabled()) {
            log.info("response ==> {}", JsonUtils.toJson(response));
        }
    }
}
