package top.verytouch.vkit.rbac.openid;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.verytouch.vkit.common.base.Response;
import top.verytouch.vkit.common.util.MapUtils;
import top.verytouch.vkit.common.util.RandomUtils;

import java.util.Map;

/**
 * openid默认授权端点
 *
 * @author verytouch
 * @since 2023/9/8 15:38
 */
@FrameworkEndpoint
@ResponseBody
@RequiredArgsConstructor
public class OpenIdTokenEndpoint {

    @SuppressWarnings("all")
    private final OpenIdService openIdService;

    /**
     * 授权，如生成二维码、跳转到第三方页面等
     */
    @GetMapping("/login/openid/authorize")
    public Response<?> authorize() {
        String secret = RandomUtils.uuid();
        String auth = openIdService.authorize(secret);
        Map<String, Object> data = MapUtils.Builder.hashMap(2)
                .put("secret", secret)
                .put("auth", auth)
                .build();
        return Response.ok(data);
    }

    /**
     * 存储code
     */
    @PostMapping("/login/openid/store")
    public Response<?> store(@RequestParam String secret, @RequestParam String code) {
        openIdService.storeCode(secret, code);
        return Response.ok();
    }

}
