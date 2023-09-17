package top.verytouch.vkit.rbac.web;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (log.isInfoEnabled()) {
            String clazz = joinPoint.getTarget().getClass().getSimpleName();
            String method = joinPoint.getSignature().getName();
            String params = Arrays.stream(args)
                    .filter(obj -> !(obj instanceof MultipartFile))
                    .filter(obj -> !(obj instanceof HttpServletRequest))
                    .filter(obj -> !(obj instanceof HttpServletResponse))
                    .map(JsonUtils::toJson)
                    .collect(Collectors.joining(", "));
            log.info("request ===> {}.{}: {}", clazz, method, params);
        }
        Object response;
        try {
            response = joinPoint.proceed(args);
            if (log.isInfoEnabled()) {
                log.info("response ===> {}", response);
            }
        } catch (Throwable e) {
            if (log.isInfoEnabled()) {
                log.info("response ===> ", e);
            }
            throw e;
        }
        return response;
    }
}
