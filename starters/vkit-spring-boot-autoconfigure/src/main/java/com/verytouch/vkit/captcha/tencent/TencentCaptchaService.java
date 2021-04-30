package com.verytouch.vkit.captcha.tencent;

import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultRequest;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.verytouch.vkit.captcha.CaptchaEnum;
import com.verytouch.vkit.captcha.CaptchaParams;
import com.verytouch.vkit.captcha.CaptchaService;
import com.verytouch.vkit.common.base.Assert;
import com.verytouch.vkit.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 腾讯行为验证码
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Slf4j
public class TencentCaptchaService implements CaptchaService {

    private final CaptchaClient client;

    private final TencentCaptchaProperties properties;

    private static final Long OK_CODE = 1L;

    public TencentCaptchaService(TencentCaptchaProperties properties) {
        Credential credential = new Credential(properties.getSecretId(), properties.getSecretKey());

        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(properties.getEndpoint());

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        client = new CaptchaClient(credential, "", clientProfile);
        this.properties = properties;
    }

    @Override
    public CaptchaEnum getType() {
        return CaptchaEnum.TENCENT;
    }

    @Override
    public void verify(CaptchaParams params) {
        TencentCaptchaParams p = Assert.instanceOf(params, TencentCaptchaParams.class, "参数类型错误，必须为TencentCaptchaParams");
        Assert.nonBlank(p.getTicket(), "ticket不能为空");
        Assert.nonBlank(p.getRandStr(), "randStr不能为空");
        Assert.nonBlank(p.getUserIp(), "userIp不能为空");

        DescribeCaptchaResultRequest req = new DescribeCaptchaResultRequest();
        req.setCaptchaType(properties.getCaptchaType());
        req.setTicket(p.getTicket());
        req.setUserIp(p.getUserIp());
        req.setRandstr(p.getRandStr());
        req.setCaptchaAppId(properties.getAppId());
        req.setAppSecretKey(properties.getAppKey());

        try {
            DescribeCaptchaResultResponse res = client.DescribeCaptchaResult(req);
            if (!OK_CODE.equals(res.getCaptchaCode())) {
                throw new BusinessException(res.getCaptchaMsg());
            }
        } catch (Exception e) {
            log.error("腾讯验证码获取结果失败", e);
            throw new BusinessException("腾讯验证码获取结果失败");
        }
    }

    @Override
    public void verify(Map<String, String> params) {
        TencentCaptchaParams tencentCaptchaParams = new TencentCaptchaParams();
        tencentCaptchaParams.setTicket(params.get("ticket"));
        tencentCaptchaParams.setRandStr(params.get("randStr"));
        tencentCaptchaParams.setUserIp(params.get("userIp"));
        verify(tencentCaptchaParams);
    }
}
