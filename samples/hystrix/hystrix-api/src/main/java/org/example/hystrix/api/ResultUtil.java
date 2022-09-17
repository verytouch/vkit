package org.example.hystrix.api;

import java.util.HashMap;
import java.util.Map;

public class ResultUtil extends HashMap {
	public static final String STATE = "state";
	public static final String STATE_OK = "ok";
	public static final String STATE_FAIL = "fail";
	public static final String MSG = "msg";
	private static final String CODE_SUCCESS = "200";
	private static final String MSG_SUCCESS = "操作成功";

	public static ResultUtil create(Object key, Object value) {
		return new ResultUtil().set(key, value);
	}

	public static ResultUtil create() {
		return new ResultUtil();
	}

	public static ResultUtil ok() {
		return new ResultUtil().setOk().set("code",CODE_SUCCESS).set("msg",MSG_SUCCESS);
	}

	public static ResultUtil ok(Object key, Object value) {
		return ok().set(key, value);
	}

	public static ResultUtil fail() {
		return new ResultUtil().setFail();
	}

	public static ResultUtil fail(Object key, Object value) {
		return fail().set(key, value);
	}

	public ResultUtil setOk() {
		super.put("state", "ok");
		return this;
	}

	public ResultUtil setFail() {
		super.put("state", "fail");
		return this;
	}

	public boolean isOk() {
		Object state = get("state");
		if ("ok".equals(state)) {
			return true;
		}
		if ("fail".equals(state)) {
			return false;
		}
		throw new IllegalStateException("调用 isOk() 之前，必须先调用 ok()、fail() 或者 setOk()、setFail() 方法");
	}

	public boolean isFail() {
		Object state = get("state");
		if ("fail".equals(state)) {
			return true;
		}
		if ("ok".equals(state)) {
			return false;
		}
		throw new IllegalStateException("调用 isFail() 之前，必须先调用 ok()、fail() 或者 setOk()、setFail() 方法");
	}

    public ResultUtil code(String code) {
        return set("code", code);
    }

    public ResultUtil msg(Object message) {
        return set("msg", message);
    }

    public ResultUtil data(Object data) {
        return set("data", data);
    }

	public ResultUtil set(Object key, Object value) {
		super.put(key, value);
		return this;
	}

	public ResultUtil set(Map map) {
		super.putAll(map);
		return this;
	}

	public ResultUtil set(ResultUtil ret) {
		super.putAll(ret);
		return this;
	}

	public ResultUtil delete(Object key) {
		super.remove(key);
		return this;
	}

	public <T> T getAs(Object key) {
		return (T) get(key);
	}

	public String getStr(Object key) {
		Object s = get(key);
		return s != null ? s.toString() : null;
	}

	public Integer getInt(Object key) {
		Number n = (Number) get(key);
		return n != null ? Integer.valueOf(n.intValue()) : null;
	}

	public Long getLong(Object key) {
		Number n = (Number) get(key);
		return n != null ? Long.valueOf(n.longValue()) : null;
	}

	public Number getNumber(Object key) {
		return (Number) get(key);
	}

	public Boolean getBoolean(Object key) {
		return (Boolean) get(key);
	}

	public boolean notNull(Object key) {
		return get(key) != null;
	}

	public boolean isNull(Object key) {
		return get(key) == null;
	}

	public boolean isTrue(Object key) {
		Object value = get(key);
		return ((value instanceof Boolean)) && (((Boolean) value).booleanValue() == true);
	}

	public boolean isFalse(Object key) {
		Object value = get(key);
		return ((value instanceof Boolean)) && (!((Boolean) value).booleanValue());
	}

	@Override
	public boolean equals(Object ret) {
		return ((ret instanceof ResultUtil)) && (super.equals(ret));
	}

	@Override
	public String toString() {
		return "ResultUtil [toString()=" + super.toString() + "]";
	}

	public static void main(String[] args) {
		System.out.println(ResultUtil.ok());
		System.out.println(ResultUtil.fail());
	}
}