package top.verytouch.vkit.mydoc.builder.file;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.builder.file.OutputStreamDocBuilder;
import top.verytouch.vkit.mydoc.config.ConfigStorage;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiField;
import top.verytouch.vkit.mydoc.model.ApiGroup;
import top.verytouch.vkit.mydoc.model.ApiModel;
import top.verytouch.vkit.mydoc.model.ApiOperation;
import top.verytouch.vkit.mydoc.util.ApiUtil;
import top.verytouch.vkit.mydoc.util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 生成可导入postman的collection文件
 *
 * @author verytouch
 * @since 2021-12
 */
public class PostmanDocBuilder extends OutputStreamDocBuilder {

    private List<Map<String, String>> headers;

    public PostmanDocBuilder(AnActionEvent event) {
        super(event, DocType.POSTMAN);
    }

    @Override
    protected OutputStream buildOutputStream(ApiModel model) throws IOException {
        initHeaders(model.getConfig());
        List<Map<String, Object>> groupList = JsonUtil.newArray();
        for (ApiGroup group : model.getData()) {
            List<Map<String, Object>> apiList = JsonUtil.newArray();
            for (ApiOperation apiOperation : group.getOperationList()) {
                apiList.add(buildApiOperation(group, apiOperation, model.getConfig()));
            }
            groupList.add(JsonUtil.newObject()
                    .putOne("name", group.getName())
                    .putOne("item", apiList));
        }
        Map<String, Object> info = JsonUtil.newObject()
                .putOne("name", "myDoc_" + model.getName())
                .putOne("schema", "https://schema.getpostman.com/json/collection/v2.1.0/collection.json");
        Map<String, Object> collection = JsonUtil.newObject()
                .putOne("info", info)
                .putOne("item", groupList);
        OutputStream outputStream = Files.newOutputStream(Paths.get(getOutPath(model)));
        JsonUtil.toJson(collection, outputStream);
        return outputStream;
    }

    private void initHeaders(ConfigStorage config) {
        headers = config.realHeaders().entrySet().stream()
                .map(entry -> JsonUtil.newObject("key", entry.getKey())
                        .putOne("value", entry.getValue())
                        .putOne("type", "text"))
                .collect(Collectors.toList());
    }

    private Map<String, Object> buildApiOperation(ApiGroup group, ApiOperation apiOperation, ConfigStorage config) {
        String contentType = Objects.toString(apiOperation.getContentType(), "");
        // url
        Map<String, Object> url = JsonUtil.newObject()
                .putOne("host", config.getApiServer())
                .putOne("path", processPathArray(group.getPath() + apiOperation.getPath()))
                .putOne("raw", config.getApiServer() + group.getPath() + apiOperation.getPath())
                .putOne("query", contentType.startsWith("multipart/form-data") ?
                        null : processParam(apiOperation.getRequestParam(), ""))
                .putOne("variable", processParam(apiOperation.getPathVariable(), "1"));
        // method，由于导入时GET的body会消失，所以尽量不用GET
        String method = Arrays.stream(apiOperation.getMethod().split(","))
                .filter(m -> !"GET".equals(m))
                .findAny()
                .orElse(apiOperation.getMethod());
        // body
        Map<String, Object> body = null;
        if (contentType.startsWith("application/json")) {
            Object bodyExample = ApiUtil.buildBodyExample(apiOperation.getRequestBody());
            body = JsonUtil.newObject()
                    .putOne("mode", "raw")
                    .putOne("raw", JsonUtil.toJson(bodyExample, true, true))
                    .putOne("options", JsonUtil.newObject("raw",
                            JsonUtil.newObject("language", "json")));
        } else if (contentType.startsWith("multipart/form-data")) {
            body = JsonUtil.newObject()
                    .putOne("mode", "formdata")
                    .putOne("formdata", processParam(apiOperation.getRequestParam(), ""));
        }
        // api
        Map<String, Object> request = JsonUtil.newObject()
                .putOne("method", method)
                .putOne("header", headers)
                .putOne("body", body)
                .putOne("url", url);
        return JsonUtil.<String, Object>newObject("name", apiOperation.getName())
                .putOne("request", request);
    }

    /**
     * url转换为数组
     *
     * @param path url
     */
    private List<String> processPathArray(String path) {
        if (StringUtils.isBlank(path)) {
            return new ArrayList<>();
        }
        path = path.replaceAll("\\{(\\w+)}", ":$1");
        return Stream.of(path.split("/"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    /**
     * 处理请求参数为kev -> value格式
     *
     * @param params     参数
     * @param defaultVal 默认值
     */
    private List<JsonUtil.JsonObject<String, String>> processParam(List<ApiField> params, String defaultVal) {
        if (CollectionUtils.isEmpty(params)) {
            return null;
        }
        return params.stream()
                .map(f -> JsonUtil.newObject("key", f.getName())
                        .putOne("type", "MultipartFile".equals(f.getType()) ? "file" : "text")
                        .putOne("value", defaultVal))
                .collect(Collectors.toList());
    }
}
