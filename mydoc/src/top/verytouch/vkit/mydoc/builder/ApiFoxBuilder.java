package top.verytouch.vkit.mydoc.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.lang3.StringUtils;
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
        String apiFoxConfig = BuilderUtil.getConfig(event).getApiFox();
        if (StringUtils.isBlank(apiFoxConfig)) {
            throw new RuntimeException("please config apiFox");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> apiFoxConfigObj = JsonUtil.toObject(apiFoxConfig, Map.class);
        String apiFoxProject = apiFoxConfigObj.getOrDefault("project", "").toString();
        if (StringUtils.isBlank(apiFoxProject)) {
            throw new RuntimeException("please config apiFox project");
        }
        String apiFoxToken = apiFoxConfigObj.getOrDefault("token", "").toString();
        if (StringUtils.isBlank(apiFoxToken)) {
            throw new RuntimeException("please config apiFox token");
        }
        String folder = apiFoxConfigObj.getOrDefault("folder", "").toString();
        String status = apiFoxConfigObj.getOrDefault("status", "developing").toString();
        JsonUtil.JsonObject<String, Object> openApi = OpenApiUtil.buildModel(model, (group, operation) -> JsonUtil.newObject()
                .putOne("x-apifox-folder", StringUtils.isBlank(folder) ? group.getName() : folder + "/" + group.getName())
                .putOne("x-apifox-status", StringUtils.isBlank(status) ? "developing" : status));
        Map<String, Object> data = new HashMap<>();
        data.put("importFormat", "openapi");
        // name both merge ignore
        data.put("apiOverwriteMode", apiFoxConfigObj.getOrDefault("apiOverwriteMode", "ignore"));
        data.put("data", openApi.toJson());
        String res = new HttpUtil("https://api.apifox.cn/api/v1/projects/" + apiFoxProject + "/import-data")
                .addHeader("X-Apifox-Version", "2022-11-16")
                .addHeader("Authorization", "Bearer " + apiFoxToken)
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
