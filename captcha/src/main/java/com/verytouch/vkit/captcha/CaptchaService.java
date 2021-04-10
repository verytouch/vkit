package com.verytouch.vkit.captcha;

import java.util.Map;

public interface CaptchaService {

    CaptchaEnum getType();

    void verify(CaptchaParams params) throws CaptchaException;

    void verify(Map<String, String> params) throws CaptchaException;
}
