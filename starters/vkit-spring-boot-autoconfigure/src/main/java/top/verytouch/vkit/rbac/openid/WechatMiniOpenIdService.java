package top.verytouch.vkit.rbac.openid;

import lombok.Data;
import top.verytouch.vkit.common.exception.BusinessException;
import top.verytouch.vkit.common.util.HttpUtils;
import top.verytouch.vkit.common.util.JsonUtils;
import top.verytouch.vkit.common.util.MapUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * 微信小程序登录
 *
 * @author verytouch
 * @since 2023/9/8 20:01
 */
public abstract class WechatMiniOpenIdService implements OpenIdService {

    private Token token;

    @Override
    public Object preAuthorize(String secret, Object platform) {
        try {
            return getQRCode(secret);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    public abstract String getAppid();

    public abstract String getAppSecret();

    public abstract Map<String, Object> getOtherParams();

    private String getQRCode(String secret) throws Exception {
        if (this.token == null || this.token.expired()) {
            this.getToken();
        }
        MapUtils.Builder<String, Object> bodyBuilder = MapUtils.Builder.hashMap();
        Map<String, Object> otherParams = this.getOtherParams();
        if (otherParams != null) {
            otherParams.forEach(bodyBuilder::put);
        }
        Map<String, Object> body = bodyBuilder.build();
        body.putIfAbsent("scene", secret);
        HttpUtils.HttpResponse response = new HttpUtils("https://api.weixin.qq.com/wxa/getwxacodeunlimit")
                .addParam("access_token", this.token.value)
                .body(JsonUtils.toJson(body).getBytes(StandardCharsets.UTF_8))
                .addHeader("Content-Type", "application/json")
                .method("POST")
                .request();
        if (response.getHeaders().get("Content-Type").startsWith("image")) {
            return Base64.getEncoder().encodeToString(response.getBytes());
        } else {
            Object errMsg = JsonUtils.mapFromJson(response.getString()).getOrDefault("errmsg", "获取二维码失败");
            throw new BusinessException(errMsg.toString());
        }
    }

    private void getToken() {
        String res = new HttpUtils("https://api.weixin.qq.com/cgi-bin/token")
                .addParam("grant_type", "client_credential")
                .addParam("appid", this.getAppid())
                .addParam("secret", this.getAppSecret())
                .get();
        Map<String, Object> map = JsonUtils.mapFromJson(res);
        String accessToken = (String) map.get("access_token");
        long expiresIn = Long.parseLong(map.getOrDefault("expires_in", "0").toString());
        this.token = new Token(accessToken, System.currentTimeMillis() + expiresIn * 1000);
    }

    @Data
    private static class Token {
        private String value;
        private long expireAt;

        public Token(String value, long expireAt) {
            this.value = value;
            this.expireAt = expireAt - 10000;
        }

        public boolean expired() {
            return System.currentTimeMillis() >= expireAt;
        }
    }

}
