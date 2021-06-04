package top.verytouch.vkit.common.base;

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
public class Response<T> {

    private int code;
    private T data;
    private String msg;
    private String traceId;
    private Map<String, Object> extra;

    /**
     * 构造接口返回对象
     *
     * @param code  状态代码
     * @param data  数据
     * @param msg   提示
     * @param extra 其他数据
     * @return Response
     */
    public static <T> Response<T> of(int code, T data, String msg, Map<String, Object> extra) {
        Response<T> res = new Response<>();
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
     * @return Response
     */
    public static Response<String> of(ApiCode apiCode) {
        return of(apiCode.getCode(), null, apiCode.getDesc(), null);
    }

    /**
     * 构造接口返回对象 - 成功
     *
     * @param data 业务数据
     * @return Response
     */
    public static <T> Response<T> ok(T data) {
        return of(ApiCode.OK.getCode(), data, ApiCode.OK.getDesc(), null);
    }

    /**
     * 构造接口返回对象 - 成功
     *
     * @param data  业务数据
     * @param extra 其他数据
     * @return Response
     */
    public static <T> Response<T> ok(T data, Map<String, Object> extra) {
        return of(ApiCode.OK.getCode(), data, ApiCode.OK.getDesc(), extra);
    }

    /**
     * 构造接口返回对象 - 成功
     *
     * @param data 业务数据
     * @param msg  提示
     * @return Response
     */
    public static <T> Response<T> ok(T data, String msg) {
        return of(ApiCode.OK.getCode(), data, msg, null);
    }

    /**
     * 构造接口返回对象
     *
     * @return Response
     */
    public static <T> Response<T> ok() {
        return of(ApiCode.OK.getCode(), null, ApiCode.OK.getDesc(), null);
    }

    /**
     * 构造接口返回对象 - 失败
     *
     * @param code 错误代码
     * @param msg  提示
     * @return Response
     */
    public static Response<String> error(int code, String msg) {
        return of(code, null, msg, null);
    }

    /**
     * 构造接口返回对象 - 失败
     *
     * @param msg 提示
     * @return Response
     */
    public static Response<String> error(String msg) {
        return of(ApiCode.ERROR.getCode(), null, msg, null);
    }

}
