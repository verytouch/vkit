package top.verytouch.vkit.rbac.openid;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Response<?> authorize(@RequestParam Object platform) {
        String secret = RandomUtils.uuid();
        openIdService.store(secret, String.valueOf(platform));
        Map<String, Object> data = MapUtils.Builder.hashMap()
                .put("secret", secret)
                .put("data", openIdService.preAuthorize(secret, platform))
                .build();
        return Response.ok(data);
    }

    /**
     * 授权回调，用户在第三方页面授权后回调本接口
     */
    @GetMapping("/login/openid/callback")
    public Response<?> callback(@RequestParam String openid, @RequestParam String secret) {
        String platform = openIdService.get(secret);
        if (platform == null) {
            return Response.error("unknown account");
        }
        UserDetails details = openIdService.loadUserByOpenId(openid);
        if (details == null) {
            return Response.error("not bind any account");
        }
        openIdService.store(secret, openid);
        return Response.ok(secret);
    }

}
