package top.verytouch.vkit.captcha;

import top.verytouch.vkit.common.base.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码上下文，聚合了所有验证码实验
 *
 * @author verytouch
 * @since 2021/04/29 10:10
 */
public class CaptchaContext {

    /*
     * 验证码服务
     */
    private static final Map<CaptchaEnum, CaptchaService> serviceMap = new ConcurrentHashMap<>();

    /**
     * 添加一个验证码实现
     *
     * @param service 验证码服务
     */
    public static void addService(CaptchaService service) {
        Assert.nonNull(service, "验证码服务不能为空");
        serviceMap.put(service.getType(), service);
    }

    /**
     * 校验验证码
     *
     * @param type   验证码类型
     * @param params 验证码参数
     */
    public static void verify(CaptchaEnum type, Map<String, String> params) {
        Assert.nonNull(type, "验证码类型不能为空");
        Assert.nonNull(params, "验证码参数不能为空");
        CaptchaService service = serviceMap.get(type);
        Assert.nonNull(service, "验证码服务" + type.name() + "不存在");
        service.verify(params);
    }

    /**
     * 校验验证码，根据参数captcha获取CaptchaEnum，再判断由哪个具体的类校验
     *
     * @param params 验证码参数
     * @see CaptchaEnum
     */
    public static void verify(Map<String, String> params) {
        Assert.nonBlank(params.get("captcha"), "验证码类型不能为空");
        CaptchaEnum type = CaptchaEnum.valueOf(params.get("captcha"));
        Assert.nonNull(type, "验证码类型" + params.get("captcha") + "不正确");
        verify(type, params);
    }
}
