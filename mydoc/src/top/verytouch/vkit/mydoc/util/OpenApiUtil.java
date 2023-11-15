package top.verytouch.vkit.mydoc.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.model.*;
import top.verytouch.vkit.mydoc.util.JsonUtil.JsonArray;
import top.verytouch.vkit.mydoc.util.JsonUtil.JsonObject;

import java.util.List;
import java.util.UUID;

/**
 * 生成OpenApi3
 *
 * @author verytouch
 * @since 2023-11
 */
public final class OpenApiUtil {

    public static String buildModel(ApiModel model) {
        JsonObject<String, Object> paths = JsonUtil.newObject();
        for (ApiGroup group : model.getData()) {
            for (ApiOperation operation : group.getOperationList()) {
                paths.putOne(group.getPath() + operation.getPath(), buildOperation(group, operation));
            }
        }
        JsonObject<String, Object> openapi = JsonUtil.newObject()
                .putOne("openapi", "3.0.0")
                .putOne("info", JsonUtil.newObject("title", model.getName()).putOne("version", "1.0.0"))
                .putOne("definitions", JsonUtil.newObject())
                .putOne("paths", paths);
        return JsonUtil.toJson(openapi);
    }

    public static JsonObject<String, Object> buildOperation(ApiGroup group, ApiOperation operation) {
        JsonObject<String, Object> content = JsonUtil.newObject()
                .putOne("x-apifox-folder", group.getName())
                .putOne("x-apifox-status", "developing")
                .putOne("description", operation.getDesc())
                .putOne("summary", operation.getDesc())
                .putOne("operationId", UUID.randomUUID().toString())
                .putOne("parameters", buildParameters(operation))
                .putOne("requestBody", buildBody(operation.getRequestBody()))
                .putOne("responses", JsonUtil.newObject("200", buildBody(operation.getResponseBody())));
        String method = operation.getMethod().toLowerCase().split(",")[0];
        return JsonUtil.newObject().putOne(method, content);
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
                .putOne("type", getType(field))
                .putOne("name", field.getName())
                .putOne("description", field.getDesc());
        List<ApiField> children = field.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return content;
        }
        if (ClassKind.ARRAY == field.getClassKind()) {
            JsonObject<String, Object> properties = JsonUtil.newObject();
            children.forEach(child -> properties.putOne(child.getName(), buildField(child)));
            JsonObject<String, Object> items = JsonUtil.newObject()
                    .putOne("type", "object")
                    .putOne("properties", properties);
            content.putOne("items", items);
        } else {
            JsonObject<String, Object> properties = JsonUtil.newObject();
            children.forEach(child -> properties.putOne(child.getName(), buildField(child)));
            content.putOne("properties", properties);
        }
        return content;
    }

    private static JsonObject<String, Object> buildBody(List<ApiField> bodyFields) {
        JsonObject<String, Object> body = JsonUtil.newObject();
        if (CollectionUtils.isEmpty(bodyFields)) {
            return body;
        }
        JsonObject<String, Object> schema = JsonUtil.newObject();
        body.putOne("content", JsonUtil.newObject("application/json", JsonUtil.newObject("schema", schema)));
        if (bodyFields.size() == 1 &&
                bodyFields.get(0).getClassKind() == ClassKind.ARRAY &&
                StringUtils.isBlank(bodyFields.get(0).getName())) {
            // body是list
            schema.putOne("type", "array").putOne("items", buildField(bodyFields.get(0)));
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

    private static String getType(ApiField param) {
        String apiType = param.getClassKind().getOpenApiType();
        if (StringUtils.isNotBlank(apiType)) {
            return apiType;
        }
        switch (param.getType().toLowerCase()) {
            case "boolean":
                return "boolean";
            case "byte":
            case "short":
            case "int":
            case "long":
            case "integer":
                return "integer";
            case "float":
            case "double":
            case "decimal":
            case "number":
                return "number";
            default:
                return "string";
        }
    }

}
