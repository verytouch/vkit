package com.verytouch.vkit.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;

/**
 * 支付宝接口
 * 文档：https://openhome.alipay.com/docCenter/docCenter.htm
 *
 * @author verytouch
 * @since 2021/4/30 10:32
 */
public class AlipayService {

    private final AlipayProperties properties;
    private final AlipayClient alipayClient;

    public AlipayService(AlipayProperties properties) {
        this.properties = properties;
        this.alipayClient = new DefaultAlipayClient(
            properties.getGatewayUrl(),
            properties.getAppId(),
            properties.getPrivateKey(),
            properties.getFormat(),
            properties.getCharset(),
            properties.getAlipayPublicKey(),
            properties.getSignType()
        );
    }

    /**
     * 获取用户授权地址
     * 1.前端打开该页面后跳转到支付宝登录页面，随后通过state轮询token
     * 2.用户登录支付宝并同意授权后跳转到redirectUri
     * 3.后端通过redirectUri中的code参数调用支付宝接口获取token
     * 4.后端保存state-token数据，前端查询时将token返回
     *
     * @param state 唯一标识，如uuid
     */
    public String getAuthorizeUrl(String state) {
        return String.format(
            "%s?app_id=%s&redirect_uri=%s&scope=auth_user&state=%s",
            properties.getAuthorizeUrl(),
            properties.getAppId(),
            properties.getRedirectUri(),
            state
        );
    }

    /**
     * 使用code获取token
     * alipay_user_id已作废，需要使用user_id
     */
    public AlipaySystemOauthTokenResponse getToken(String code) throws AlipayApiException {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode(code);
        return alipayClient.execute(request);
    }

    /**
     * 刷新token
     */
    public AlipaySystemOauthTokenResponse refreshToken(String refreshToken) throws AlipayApiException {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("refresh_token");
        request.setRefreshToken(refreshToken);
        return alipayClient.execute(request);
    }

    /**
     * 会员授权信息
     */
    public AlipayUserInfoShareResponse getUserInfo(String token) throws AlipayApiException {
        AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
        return alipayClient.execute(request, token);
    }
}
