package top.verytouch.vkit.mydoc.util;

import com.intellij.psi.PsiModifierListOwner;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.constant.ClassKind;
import top.verytouch.vkit.mydoc.model.ApiField;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * ApiUtil
 *
 * @author verytouch
 * @since 2021-12
 */
public class ApiUtil {

    /**
     * Mock值
     * 键为openApiType或java类简称
     * 值为mock值
     */
    private static final JsonUtil.JsonObject<String, Object> MOCK_MAP = JsonUtil.newObject()
            .putOne("boolean", false)
            .putOne("integer", 0)
            .putOne("string", "")
            .putOne("number", null)
            .putOne("object", null)
            .putOne("int", 0)
            .putOne("long", 0);

    /**
     * 是否private
     *
     * @param psiModifierListOwner 元素
     */
    public static boolean isPrivate(PsiModifierListOwner psiModifierListOwner) {
        return psiModifierListOwner.hasModifierProperty("private");
    }

    /**
     * 是否static
     *
     * @param psiModifierListOwner 元素
     */
    public static boolean isStatic(PsiModifierListOwner psiModifierListOwner) {
        return psiModifierListOwner.hasModifierProperty("static");
    }

    /**
     * 按顺序取第一个不为空的值
     *
     * @param defaultValue 所有suppliers返回都是空的，就返回defaultValue
     * @param suppliers    取值方法
     */
    @SafeVarargs
    public static <T> T getFirstNotBlankValue(T defaultValue, Supplier<T>... suppliers) {
        if (suppliers == null || suppliers.length == 0) {
            return defaultValue;
        }
        T value;
        for (Supplier<T> supplier : suppliers) {
            value = supplier.get();
            if (value != null && StringUtils.isNotBlank(value.toString())) {
                return value;
            }
        }
        return defaultValue;
    }

    /**
     * 多路径处理，返回第一个
     * {/aa, /bb} -> /aa
     * aa -> /aa
     *
     * @param multiplePath 多路径
     */
    public static String getFirstPath(String multiplePath) {
        if (StringUtils.isBlank(multiplePath)) {
            return "";
        }
        // 多路径，取第一个
        if (multiplePath.startsWith("{") && multiplePath.endsWith("}") && multiplePath.contains(",")) {
            multiplePath = multiplePath.substring(1, multiplePath.length() - 1).split(",")[0];
        }
        // 处理斜杠
        String path = Arrays.stream(multiplePath.split("/"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("/"));
        return StringUtils.isBlank(path) ? "" : "/" + path;
    }

    /**
     * 判断body是否是array
     */
    public static boolean isBodyArray(List<ApiField> bodyFields) {
        return bodyFields.size() == 1 &&
                bodyFields.get(0).getClassKind() == ClassKind.ARRAY &&
                StringUtils.isBlank(bodyFields.get(0).getName());
    }

    /**
     * 构建参数示例
     *
     * @param apiFields 参数
     */
    public static Object buildBodyExample(List<ApiField> apiFields) {
        if (CollectionUtils.isEmpty(apiFields)) {
            return null;
        }
        Object body;
        if (isBodyArray(apiFields)) {
            // body是list
            ApiField field = apiFields.get(0);
            Object item = buildBodyExample(field.getChildren());
            if (item == null) {
                // item为null时，说明list里面的是简单类型，从mock中取示例值
                String[] types = Objects.toString(field.getType(), "").split("[<>]");
                item = MOCK_MAP.get(types[types.length - 1]);
            }
            body = JsonUtil.newArray(item);
        } else {
            // body是对象，解析其字段
            JsonUtil.JsonObject<String, Object> bodyObject = JsonUtil.newObject();
            for (ApiField field : apiFields) {
                if (field.getName() == null) {
                    continue;
                }
                Object value = buildBodyExample(field.getChildren());
                // 该字段没有children，说明是简单字段或者不需要解析的字段
                if (value == null) {
                    value = getMockValue(field);
                }
                // 该字段是list，保证其value是数组
                if (field.getClassKind() == ClassKind.ARRAY && !(value instanceof JsonUtil.JsonArray)) {
                    value = value == null ? JsonUtil.newArray() : JsonUtil.newArray(value);
                }
                bodyObject.putOne(field.getName(), value);
            }
            body = bodyObject;
        }
        return body;
    }

    public static Object getMockValue(ApiField field) {
        String prefer = field.getMock();
        if (StringUtils.isBlank(prefer)) {
            if ("date".equalsIgnoreCase(field.getType())) {
                return null;
            }
            return MOCK_MAP.get(field.getOpenApiType());
        }
        try {
            switch (field.getOpenApiType()) {
                case "boolean":
                    return Boolean.parseBoolean(prefer);
                case "integer":
                    return Long.parseLong(prefer);
                case "string":
                    return prefer;
                default:
                    return null;
            }
        } catch (Exception e) {
            return MOCK_MAP.get(field.getOpenApiType());
        }
    }

}
