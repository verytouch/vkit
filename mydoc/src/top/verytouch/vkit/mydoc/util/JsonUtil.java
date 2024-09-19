package top.verytouch.vkit.mydoc.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Json处理
 *
 * @author verytouch
 * @since 2021-12
 */
public class JsonUtil {

    private static final ObjectMapper JSON = new ObjectMapper();

    /**
     * json序列化
     *
     * @param object 对象
     */
    public static String toJson(Object object) {
        return toJson(object, false, false);
    }

    /**
     * json序列化到输出流
     *
     * @param object       对象
     * @param outputStream 输出流
     */
    public static void toJson(Object object, OutputStream outputStream) {
        try {
            JSON.writeValue(outputStream, object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json序列化
     *
     * @param object      对象
     * @param includeNull 是否包括null字段
     * @param pretty      是否格式化json
     */
    public static String toJson(Object object, boolean includeNull, boolean pretty) {
        JSON.setSerializationInclusion(includeNull ? JsonInclude.Include.ALWAYS : JsonInclude.Include.NON_NULL);
        try {
            return pretty ?
                    JSON.writerWithDefaultPrettyPrinter().writeValueAsString(object) :
                    JSON.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json反序列化
     *
     * @param json  json
     * @param clazz 类
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return JSON.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转为jsonNode
     */
    public static JsonNode toJsonNode(String json) {
        try {
            return JSON.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建一个对象
     */
    public static JsonObject<String, Object> newObject() {
        return new JsonObject<>();
    }

    /**
     * 创建一个对象，并放入键值对
     *
     * @param k 键
     * @param v 值
     */
    public static <K, V> JsonObject<K, V> newObject(K k, V v) {
        JsonObject<K, V> obj = new JsonObject<>();
        return obj.putOne(k, v);
    }

    /**
     * 创建一个数组
     */
    public static <T> JsonArray<T> newArray() {
        return new JsonArray<>();
    }

    /**
     * 创建一个数组，并放入一个元素
     *
     * @param item 元素
     */
    public static <T> JsonArray<T> newArray(T item) {
        return new JsonArray<T>().addOne(item);
    }

    /**
     * 对象
     */
    public static class JsonObject<K, V> extends HashMap<K, V> {

        public JsonObject<K, V> putOne(K k, V v) {
            super.put(k, v);
            return this;
        }

        public String toJson() {
            return JsonUtil.toJson(this);
        }
    }

    /**
     * 数组
     */
    public static class JsonArray<E> extends LinkedList<E> {

        public JsonArray<E> addOne(E e) {
            super.add(e);
            return this;
        }

        public String toJson() {
            return JsonUtil.toJson(this);
        }
    }

}
