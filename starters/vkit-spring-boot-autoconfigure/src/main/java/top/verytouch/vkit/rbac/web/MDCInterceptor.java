package top.verytouch.vkit.rbac.web;

import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import top.verytouch.vkit.common.util.RandomUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MDCInterceptor implements HandlerInterceptor {

    public static final String traceId = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MDC.put(traceId, RandomUtils.uuid());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(traceId);
    }
}
