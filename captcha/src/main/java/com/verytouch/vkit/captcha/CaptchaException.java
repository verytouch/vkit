package com.verytouch.vkit.captcha;

public class CaptchaException extends Exception {

    public CaptchaException(String message) {
        super(message);
    }

    public CaptchaException(String message, Throwable cause) {
        super(message, cause);
    }
}
