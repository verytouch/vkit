package com.verytouch.vkit.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.verytouch.vkit.common.exception.BusinessException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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

}
