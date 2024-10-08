package top.verytouch.vkit.mydoc.builder.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.builder.DocBuilder;
import top.verytouch.vkit.mydoc.builder.BuilderResult;
import top.verytouch.vkit.mydoc.config.ConfigStorage;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiGroup;
import top.verytouch.vkit.mydoc.model.ApiModel;
import top.verytouch.vkit.mydoc.model.ApiOperation;
import top.verytouch.vkit.mydoc.util.HttpUtil;
import top.verytouch.vkit.mydoc.util.JsonUtil;
import top.verytouch.vkit.mydoc.util.OpenApiUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * 同步到ApiFox
 *
 * @author verytouch
 * @since 2023-11
 */
public final class ApiFoxDocBuilder extends DocBuilder {

    public ApiFoxDocBuilder(AnActionEvent event) {
        super(event, DocType.API_FOX);
    }

    @Override
    protected BuilderResult buildDoc(ApiModel model) {
        ConfigStorage config = model.getConfig();
        if (StringUtils.isBlank(config.getApiFoxProject())) {
            return BuilderResult.failed("apifox project absent");
        }
        if (StringUtils.isBlank(config.getApiFoxToken())) {
            return BuilderResult.failed("apifox token absent");
        }
        BiFunction<ApiGroup, ApiOperation, Map<String, Object>> extra = (group, operation) -> JsonUtil.newObject()
                .putOne("x-apifox-folder", StringUtils.isBlank(config.getApiFoxFolder()) ? group.getName() : config.getApiFoxFolder() + "/" + group.getName())
                .putOne("x-apifox-status", "developing");
        byte[] body = JsonUtil.newObject()
                .putOne("importFormat", "openapi")
                .putOne("apiOverwriteMode", config.getApiFoxOverwriteMode())
                .putOne("data", OpenApiUtil.buildModel(model, extra).toJson())
                .toJson()
                .getBytes(StandardCharsets.UTF_8);
        HttpUtil.HttpResponse response = new HttpUtil("https://api.apifox.cn/api/v1/projects/" + config.getApiFoxProject() + "/import-data")
                .addHeader("X-Apifox-Version", "2022-11-16")
                .addHeader("Authorization", "Bearer " + config.getApiFoxToken())
                .addHeader("Content-Type", "application/json")
                .body(body)
                .method("POST")
                .caughtRequest(null);
        if (Objects.equals(403, response.getCode())) {
            return BuilderResult.failed("illegal apifox project");
        }
        if (Objects.equals(401, response.getCode())) {
            return BuilderResult.failed("illegal apifox token");
        }
        if (!Objects.equals(200, response.getCode())) {
            return BuilderResult.failed("upload apifox failed " + response.getMsg());
        }
        JsonNode root = JsonUtil.toJsonNode(response.asString());
        if (root.get("success").asBoolean()) {
            return BuilderResult.ok("upload apifox success " + root.get("data").get("apiCollection").get("item").toString());
        }
        return BuilderResult.failed("upload apifox failed " + root.get("errorMessage").toString());
    }

}
