package com.verytouch.vkit.captcha.tencent;

import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultRequest;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.verytouch.vkit.captcha.CaptchaEnum;
import com.verytouch.vkit.captcha.CaptchaException;
import com.verytouch.vkit.captcha.CaptchaParams;
import com.verytouch.vkit.captcha.CaptchaService;
import org.springframework.util.StringUtils;

import java.util.Map;

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
    public void verify(CaptchaParams params) throws CaptchaException {
        if (!(params instanceof TencentCaptchaParams)) {
            throw new CaptchaException("参数类型错误，必须为TencentCaptchaParams");
        }
        TencentCaptchaParams p = (TencentCaptchaParams) params;
        if (StringUtils.isEmpty(p.getTicket())) {
            throw new CaptchaException("ticket不能为空");
        }
        if (StringUtils.isEmpty(p.getRandStr())) {
            throw new CaptchaException("randStr不能为空");
        }
        if (StringUtils.isEmpty(p.getUserIp())) {
            throw new CaptchaException("userIp不能为空");
        }

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
                throw new CaptchaException(res.getCaptchaMsg());
            }
        } catch (Exception e) {
            throw new CaptchaException("腾讯验证码获取结果失败", e);
        }
    }

    @Override
    public void verify(Map<String, String> params) throws CaptchaException {
        TencentCaptchaParams tencentCaptchaParams = new TencentCaptchaParams();
        tencentCaptchaParams.setTicket(params.get("ticket"));
        tencentCaptchaParams.setRandStr(params.get("randStr"));
        tencentCaptchaParams.setUserIp(params.get("userIp"));
        verify(tencentCaptchaParams);
    }
}
