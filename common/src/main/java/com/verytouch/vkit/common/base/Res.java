package com.verytouch.vkit.common.base;

import lombok.Data;

/**
 * 通用接口返回
 *
 * @author verytouch
 * @since 2020/9/17 10:36
 */
@Data
public class Res<T> {

    private int code;
    private T data;
    private String msg;

    public static <T> Res<T> of(int code, T data, String msg) {
        Res<T> res = new Res<T>();
        res.setCode(code);
        res.setData(data);
        res.setMsg(msg);
        return res;
    }

    public static Res<String> of(ErrorCodeEnum errorCodeEnum) {
        return of(errorCodeEnum.getCode(), null, errorCodeEnum.getDesc());
    }

    public static <T> Res<T> ok(T data) {
        return of(ErrorCodeEnum.OK.getCode(), data, ErrorCodeEnum.OK.getDesc());
    }

    public static Res<String> error(int code, String msg) {
        return of(code, null, msg);
    }

    public static Res<String> error(String msg) {
        return of(ErrorCodeEnum.ERROR.getCode(), null, msg);
    }

}
