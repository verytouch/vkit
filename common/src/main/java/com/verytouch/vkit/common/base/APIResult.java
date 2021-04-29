package com.verytouch.vkit.common.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 通用接口返回
 *
 * @author verytouch
 * @since 2020/9/17 10:36
 */
@Data
@Accessors(chain = true)
public class APIResult<T> {

    private int code;
    private T data;
    private String msg;
    private Map<String, Object> extra;

    /**
     * 构造接口返回对象
     *
     * @param code 状态代码
     * @param data 数据
     * @param msg 提示
     * @param extra 其他数据
     * @return APIResult
     */
    public static <T> APIResult<T> of(int code, T data, String msg, Map<String, Object> extra) {
        APIResult<T> res = new APIResult<>();
        res.setCode(code);
        res.setData(data);
        res.setMsg(msg);
        res.setExtra(extra);
        return res;
    }

    /**
     * 构造接口返回对象
     *
     * @param apiCode 接口代码
     * @return APIResult
     */
    public static APIResult<String> of(APICode apiCode) {
        return of(apiCode.getCode(), null, apiCode.getDesc(), null);
    }

    /**
     * 构造接口返回对象 - 成功
     *
     * @param data 业务数据
     * @return APIResult
     */
    public static <T> APIResult<T> ok(T data) {
        return of(APICode.OK.getCode(), data, APICode.OK.getDesc(), null);
    }

    /**
     * 构造接口返回对象 - 成功
     *
     * @param data 业务数据
     * @param extra 其他数据
     * @return APIResult
     */
    public static <T> APIResult<T> ok(T data, Map<String, Object> extra) {
        return of(APICode.OK.getCode(), data, APICode.OK.getDesc(), extra);
    }

    /**
     * 构造接口返回对象 - 成功
     *
     * @param data 业务数据
     * @param msg 提示
     * @return APIResult
     */
    public static <T> APIResult<T> ok(T data, String msg) {
        return of(APICode.OK.getCode(), data, msg, null);
    }

    /**
     * 构造接口返回对象
     *
     * @return APIResult
     */
    public static <T> APIResult<T> ok() {
        return of(APICode.OK.getCode(), null, APICode.OK.getDesc(), null);
    }

    /**
     * 构造接口返回对象 - 失败
     *
     * @param code 错误代码
     * @param msg 提示
     * @return APIResult
     */
    public static APIResult<String> error(int code, String msg) {
        return of(code, null, msg, null);
    }

    /**
     * 构造接口返回对象 - 失败
     *
     * @param msg 提示
     * @return APIResult
     */
    public static APIResult<String> error(String msg) {
        return of(APICode.ERROR.getCode(), null, msg, null);
    }

}
