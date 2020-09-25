package com.verytouch.vkit.common.base;

import lombok.Getter;

/**
 * 错误码
 *
 * @author verytouch
 * @since 2020/9/17 16:30
 */
public enum ErrorCodeEnum {

    OK(200, "请求成功"),
    UNAUTHORIZED(401, "需要登录"),
    FORBIDDEN(403, "权限不足"),
    ERROR(500, "请求失败");

    @Getter
    private final int code;
    @Getter
    private final String desc;

    ErrorCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
