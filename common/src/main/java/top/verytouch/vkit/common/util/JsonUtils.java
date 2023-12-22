package top.verytouch.vkit.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.verytouch.vkit.common.exception.BusinessException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * json工具类
 *
 * @author verytouch
 * @since 2021/04/27 14:25
 */
public final class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 序列化
     *
     * @param bean 对象
     * @return json字符串
     */
    public static String toJson(Object bean) {
        try {
            return mapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new BusinessException("json序列化失败", e);
        }
    }

    /**
     * 转换为JsonNode
     *
     * @param value 对象
     * @return JsonNode
     */
    public static JsonNode toTree(Object value) {
        return mapper.valueToTree(value);
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
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new BusinessException("json反序列化失败", e);
        }
    }

    /**
     * 反序列化
     *
     * @param json json字符串
     * @return HashMap
     */
    public static Map<String, Object> mapFromJson(String json) {
        try {
            return mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (IOException e) {
            throw new BusinessException("json反序列化失败", e);
        }
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
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new BusinessException("json反序列化失败", e);
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
            return JsonUtils.toJson(this);
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
            return JsonUtils.toJson(this);
        }
    }

}
