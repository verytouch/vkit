package com.verytouch.vkit.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * json工具类，基于gson
 *
 * @author verytouch
 * @since 2021/04/27 14:25
 */
public final class JsonUtils {

    /*
     * 实例化gson对象，单例可以更快
     */
    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    /**
     * 获取gson对象
     */
    public static Gson getGson() {
        return GSON;
    }

    /**
     * 序列化
     *
     * @param bean 对象
     * @return json字符串
     */
    public static String toJson(Object bean) {
        return GSON.toJson(bean);
    }

    /**
     * 转换为JsonObject对象
     *
     * @param src json字符串或对象
     * @return JsonObject对象
     */
    public static JsonObject toJsonObject(Object src) {
        return GSON.toJsonTree(src).getAsJsonObject();
    }

    /**
     * 转换为JsonArray对象
     *
     * @param src json字符串或数组
     * @return JsonArray对象
     */
    public static JsonArray toJsonArray(Object src) {
        return GSON.toJsonTree(src).getAsJsonArray();
    }

    /**
     * 反序列化
     *
     * @param json  json字符串
     * @param clazz 对象的class
     * @param <T>   对象类型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * 反序列化为List
     *
     * @param json  json字符串
     * @param clazz 对象的class
     * @param <T>   对象类型
     * @return 对象的List
     */
    public static <T> List<T> listFromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{clazz};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });
    }
}
