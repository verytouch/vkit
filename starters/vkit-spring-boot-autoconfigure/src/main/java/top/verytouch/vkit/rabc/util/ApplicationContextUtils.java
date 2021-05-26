package top.verytouch.vkit.rabc.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

/**
 * ApplicationContext包装类
 *
 * @author verytouch
 * @since 2021/5/26 14:07
 */
public class ApplicationContextUtils {

    /**
     * 在top.verytouch.vkit.rabc.RbacAutoConfiguration中赋值
     */
    private static ApplicationContext context;

    public static void init(ApplicationContext context) {
        ApplicationContextUtils.context = context;
    }

    public static ApplicationContext context() {
        return context;
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static void publishEvent(ApplicationEvent event) {
        context.publishEvent(event);
    }
}
