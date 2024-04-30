package top.verytouch.vkit.mydoc.util;

import org.apache.commons.collections.CollectionUtils;
import top.verytouch.vkit.mydoc.constant.ClassKind;
import top.verytouch.vkit.mydoc.model.ApiField;
import top.verytouch.vkit.mydoc.model.ApiGroup;
import top.verytouch.vkit.mydoc.model.ApiModel;
import top.verytouch.vkit.mydoc.model.ApiOperation;
import top.verytouch.vkit.mydoc.util.JsonUtil.JsonArray;
import top.verytouch.vkit.mydoc.util.JsonUtil.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * 生成OpenApi3
 *
 * @author verytouch
 * @since 2023-11
 */
public final class OpenApiUtil {

    /**
     * 生成openapi文档
     * extra对每个接口添加定制属性
     */
    @SuppressWarnings("unchecked")
    public static JsonObject<String, Object> buildModel(ApiModel model, BiFunction<ApiGroup, ApiOperation, Map<String, Object>> extra) {
        JsonObject<String, Object> paths = JsonUtil.newObject();
        for (ApiGroup group : model.getData()) {
            for (ApiOperation operation : group.getOperationList()) {
                String path = group.getPath() + operation.getPath();
                paths.putIfAbsent(path, JsonUtil.newObject());
                ((JsonObject<String, Object>) paths.get(path)).putAll(buildOperation(group, operation, extra));
            }
        }
        return JsonUtil.newObject()
                .putOne("openapi", "3.0.0")
                .putOne("info", JsonUtil.newObject("title", model.getName()).putOne("version", "1.0.0"))
                .putOne("paths", paths);
    }

    /**
     * 生成openapi文档
     */
    public static JsonObject<String, Object> buildModel(ApiModel model) {
        return buildModel(model, null);
    }

    private static JsonObject<String, Object> buildOperation(ApiGroup group, ApiOperation operation, BiFunction<ApiGroup, ApiOperation, Map<String, Object>> extra) {
        JsonObject<String, Object> content = JsonUtil.newObject()
                .putOne("description", operation.getDesc())
                .putOne("summary", operation.getName())
                .putOne("operationId", UUID.randomUUID().toString())
                .putOne("parameters", buildParameters(operation))
                .putOne("requestBody", buildRequestBody(operation))
                .putOne("responses", JsonUtil.newObject("200", buildBody(operation.getResponseBody(), "application/json")));
        if (extra != null) {
            Map<String, Object> map = extra.apply(group, operation);
            content.putAll(map);
        }
        JsonObject<String, Object> methods = JsonUtil.newObject();
        String[] httpMethods = operation.getMethod().toLowerCase().split(",");
        for (String method : httpMethods) {
            methods.putOne(method.toLowerCase(), content);
        }
        return methods;
    }

    private static JsonArray<Object> buildParameters(ApiOperation operation) {
        JsonArray<Object> parameters = JsonUtil.newArray();
        // path
        if (CollectionUtils.isNotEmpty(operation.getPathVariable())) {
            operation.getPathVariable().forEach(field -> parameters.addOne(buildField(field, "path")));
        }
        // param
        if (CollectionUtils.isNotEmpty(operation.getRequestParam())) {
            operation.getRequestParam().forEach(field -> parameters.addOne(buildField(field, "query")));
        }
        return parameters;
    }

    private static JsonObject<String, Object> buildField(ApiField field, String in) {
        return JsonUtil.newObject()
                .putOne("in", in)
                .putOne("name", field.getName())
                .putOne("required", field.getRequired())
                .putOne("description", field.getDesc())
                .putOne("schema", buildField(field));
    }

    private static JsonObject<String, Object> buildField(ApiField field) {
        JsonObject<String, Object> content = JsonUtil.newObject()
                .putOne("type", field.getOpenApiType())
                .putOne("name", field.getName())
                .putOne("description", field.getDesc());
        List<ApiField> children = field.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return content;
        }
        JsonObject<String, Object> properties = JsonUtil.newObject();
        if (ClassKind.ARRAY == field.getClassKind()) {
            children.forEach(child -> properties.putOne(child.getName(), buildField(child)));
            JsonObject<String, Object> items = JsonUtil.newObject()
                    .putOne("type", "object")
                    .putOne("properties", properties);
            content.putOne("items", items);
        } else {
            children.forEach(child -> properties.putOne(child.getName(), buildField(child)));
            content.putOne("properties", properties);
        }
        return content;
    }

    private static JsonObject<String, Object> buildRequestBody(ApiOperation operation) {
        boolean isFile = CollectionUtils.isNotEmpty(operation.getRequestFile());
        List<ApiField> bodyFields = isFile ? operation.getRequestFile() : operation.getRequestBody();
        String contentType = isFile ? "multipart/form-data" : "application/json";
        return buildBody(bodyFields, contentType);
    }

    private static JsonObject<String, Object> buildBody(List<ApiField> bodyFields, String contentType) {
        JsonObject<String, Object> body = JsonUtil.newObject();
        if (CollectionUtils.isEmpty(bodyFields)) {
            return body;
        }
        JsonObject<String, Object> schema = JsonUtil.newObject();
        body.putOne("content", JsonUtil.newObject(contentType, JsonUtil.newObject("schema", schema)));
        if (ApiUtil.isBodyArray(bodyFields)) {
            // body是list
            schema.putAll(buildField(bodyFields.get(0)));
        } else {
            // body是对象，解析其字段
            JsonObject<String, Object> properties = JsonUtil.newObject();
            for (ApiField field : bodyFields) {
                if (field.getName() == null) {
                    continue;
                }
                properties.putOne(field.getName(), buildField(field));
            }
            schema.putOne("type", "object").putOne("properties", properties);
        }
        return body;
    }

}
