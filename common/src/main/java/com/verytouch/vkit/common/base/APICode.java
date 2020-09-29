package com.verytouch.vkit.common.base;

import lombok.Getter;

/**
 * 错误码
 * 第一位：错误级别(1=系统级别，2=业务级)
 * 2-3位：业务模块代码
 * 4-5位：错误代码
 *
 * @author verytouch
 * @since 2020/9/17 16:30
 */
public enum APICode {

    OK(200, "请求成功"),
    ERROR(500, "请求失败"),
    SYS_UNAUTHORIZED(10001, "需要登录"),
    SYS_FORBIDDEN(10002, "权限不足"),
    SYS_LIMIT_ACCOUNT(10101, "账号请求超过限制"),
    SYS_LIMIT_IP(10102, "IP请求超过限制"),
    SYS_DISABLE_ACCOUNT(10201, "账号被禁用"),
    SYS_DISABLE_IP(10202, "IP被禁用"),
    SYS_UPGRADE(10301, "接口维护"),
    SYS_ABANDON(10302, "接口停用"),
    PARAM_ABSENT(20001, "缺少参数"),
    PARAM_ERROR(20002, "参数错误");

    @Getter
    private final int code;
    @Getter
    private final String desc;

    APICode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
