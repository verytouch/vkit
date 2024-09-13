package top.verytouch.vkit.mydoc.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.config.ConfigStorage;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.util.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 同步到ApiFox
 *
 * @author verytouch
 * @since 2023-11
 */
public final class ApiFoxBuilder extends DocBuilder {

    public ApiFoxBuilder(AnActionEvent event) {
        super(event, DocType.API_FOX);
    }

    @Override
    protected void buildDoc() {
        ConfigStorage config = BuilderUtil.getConfig(event);
        if (StringUtils.isBlank(config.getApiFoxProject())) {
            throw new RuntimeException("please config apiFox project");
        }
        if (StringUtils.isBlank(config.getApiFoxToken())) {
            throw new RuntimeException("please config apiFox token");
        }
        JsonUtil.JsonObject<String, Object> openApi = OpenApiUtil.buildModel(model, (group, operation) -> JsonUtil.newObject()
                .putOne("x-apifox-folder", StringUtils.isBlank(config.getApiFoxFolder()) ? group.getName() : config.getApiFoxFolder() + "/" + group.getName())
                .putOne("x-apifox-status", "developing"));
        Map<String, Object> data = new HashMap<>();
        data.put("importFormat", "openapi");
        // name both merge ignore
        data.put("apiOverwriteMode", config.getApiFoxOverwriteMode());
        data.put("data", openApi.toJson());
        String res = new HttpUtil("https://api.apifox.cn/api/v1/projects/" + config.getApiFoxProject() + "/import-data")
                .addHeader("X-Apifox-Version", "2022-11-16")
                .addHeader("Authorization", "Bearer " + config.getApiFoxToken())
                .addHeader("Content-Type", "application/json")
                .body(JsonUtil.toJson(data).getBytes(StandardCharsets.UTF_8))
                .post();
        NotifyUtil.info(event.getProject(), res);
        @SuppressWarnings("unchecked")
        Map<String, Object> object = JsonUtil.toObject(res, Map.class);
        if (Boolean.TRUE.toString().equals(object.getOrDefault("success", "").toString())) {
            return;
        }
        throw new RuntimeException(object.getOrDefault("errorMessage", "").toString());
    }

    @Override
    protected String getSuccessMessage() {
        return null;
    }

}
