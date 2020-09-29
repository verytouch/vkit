package com.verytouch.vkit.common.base;

import lombok.Data;

import java.util.Map;

/**
 * 通用接口返回
 *
 * @author verytouch
 * @since 2020/9/17 10:36
 */
@Data
public class APIResult<T> {

    private int code;
    private T data;
    private String msg;
    private Map<String, Object> extra;

    public static <T> APIResult<T> of(int code, T data, String msg, Map<String, Object> extra) {
        APIResult<T> res = new APIResult<>();
        res.setCode(code);
        res.setData(data);
        res.setMsg(msg);
        res.setExtra(extra);
        return res;
    }

    public static APIResult<String> of(APICode apiCode) {
        return of(apiCode.getCode(), null, apiCode.getDesc(), null);
    }

    public static <T> APIResult<T> ok(T data) {
        return of(APICode.OK.getCode(), data, APICode.OK.getDesc(), null);
    }

    public static <T> APIResult<T> ok(T data, Map<String, Object> extra) {
        return of(APICode.OK.getCode(), data, APICode.OK.getDesc(), extra);
    }

    public static <T> APIResult<T> ok(T data, String msg) {
        return of(APICode.OK.getCode(), data, msg, null);
    }

    public static APIResult<String> error(int code, String msg) {
        return of(code, null, msg, null);
    }

    public static APIResult<String> error(String msg) {
        return of(APICode.ERROR.getCode(), null, msg, null);
    }

}
