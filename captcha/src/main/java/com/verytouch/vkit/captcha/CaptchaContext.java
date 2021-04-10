package com.verytouch.vkit.captcha;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CaptchaContext {

    private static final Map<CaptchaEnum, CaptchaService> serviceMap = new ConcurrentHashMap<>();

    public static void addService(CaptchaService service) {
        Objects.requireNonNull(service, "CaptchaService cannot be null");
        serviceMap.put(service.getType(), service);
    }

    public static void verify(CaptchaEnum type, Map<String, String> params) throws CaptchaException {
        Objects.requireNonNull(type, "CaptchaEnum cannot be null");
        Objects.requireNonNull(params, "params cannot be null");
        CaptchaService service = serviceMap.get(type);
        if (service == null) {
            throw new CaptchaException("验证码服务" + type.name() + "不存在");
        }
        service.verify(params);
    }

    public static void verify(Map<String, String> params) throws CaptchaException {
        CaptchaEnum type = CaptchaEnum.valueOf(params.get("captcha"));
        Objects.requireNonNull(type, "验证码类型" + params.get("captcha") + "不正确");
        verify(type, params);
    }
}
