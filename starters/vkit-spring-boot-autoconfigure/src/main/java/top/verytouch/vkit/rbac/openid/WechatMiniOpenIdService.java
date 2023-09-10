package top.verytouch.vkit.rbac.openid;

import lombok.extern.slf4j.Slf4j;
import top.verytouch.vkit.common.base.Assert;
import top.verytouch.vkit.common.exception.BusinessException;
import top.verytouch.vkit.common.util.HttpUtils;
import top.verytouch.vkit.common.util.JsonUtils;
import top.verytouch.vkit.common.util.MapUtils;
import top.verytouch.vkit.common.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * 微信小程序登录
 *
 * @author verytouch
 * @since 2023/9/8 20:01
 */
@Slf4j
public abstract class WechatMiniOpenIdService implements OpenIdService {

    private Token token;

    protected abstract String getAppid();

    protected abstract String getAppSecret();

    protected abstract Map<String, Object> getOtherParams();

    @Override
    public String authorize(String secret) {
        if (this.token == null || this.token.expired()) {
            this.getToken();
        }
        MapUtils.Builder<String, Object> bodyBuilder = MapUtils.Builder.hashMap();
        Map<String, Object> otherParams = this.getOtherParams();
        if (otherParams != null) {
            otherParams.forEach(bodyBuilder::put);
        }
        Map<String, Object> body = bodyBuilder.put("scene", secret).build();
        HttpUtils.HttpResponse response;
        try {
            response = new HttpUtils("https://api.weixin.qq.com/wxa/getwxacodeunlimit")
                    .addParam("access_token", this.token.value)
                    .body(JsonUtils.toJson(body).getBytes(StandardCharsets.UTF_8))
                    .addHeader("Content-Type", "application/json")
                    .method("POST")
                    .request();
        } catch (Exception e) {
            log.error("生成微信小程序二维码失败", e);
            throw new BusinessException("请求失败");
        }
        if (response.getHeaders().get("Content-Type").startsWith("image")) {
            return Base64.getEncoder().encodeToString(response.getBytes());
        } else {
            Object errMsg = JsonUtils.mapFromJson(response.getString()).getOrDefault("errmsg", "获取二维码失败");
            throw new BusinessException(errMsg.toString());
        }
    }

    @Override
    public String getOpenid(String code) {
        return getSession(code).getOrDefault("openid", "").toString();
    }

    /**
     * code2Session
     */
    public Map<String, Object> getSession(String code) {
        String json = new HttpUtils("https://api.weixin.qq.com/sns/jscode2session")
                .addParam("appid", this.getAppid())
                .addParam("secret", this.getAppSecret())
                .addParam("grant_type", "authorization_code")
                .addParam("js_code", code)
                .get();
        Map<String, Object> map = JsonUtils.mapFromJson(json);
        String openid = map.getOrDefault("openid", "").toString();
        if (StringUtils.isBlank(openid)) {
            String message = map.getOrDefault("errmsg", "获取openid失败").toString();
            log.error("获取openid失败: {}", message);
            throw new BusinessException(message);
        }
        return map;
    }

    /**
     * 获取token，用来获取小程序码
     */
    private void getToken() {
        String res = new HttpUtils("https://api.weixin.qq.com/cgi-bin/token")
                .addParam("grant_type", "client_credential")
                .addParam("appid", this.getAppid())
                .addParam("secret", this.getAppSecret())
                .get();
        Map<String, Object> map = JsonUtils.mapFromJson(res);
        String accessToken = (String) map.get("access_token");
        Assert.nonBlank(accessToken, "获取二维码失败");
        long expiresIn = Long.parseLong(map.getOrDefault("expires_in", "0").toString());
        this.token = new Token(accessToken, System.currentTimeMillis() + expiresIn * 1000);
    }

    private static class Token {
        private final String value;
        private final long expireAt;

        public Token(String value, long expireAt) {
            this.value = value;
            this.expireAt = expireAt - 10000;
        }

        public boolean expired() {
            return System.currentTimeMillis() >= expireAt;
        }
    }

}
