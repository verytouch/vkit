package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.VoidDocBuilder;
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
public class ApiFoxAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new VoidDocBuilder(event, DocType.API_FOX, apiModel -> {
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

            String openApi = OpenApiUtil.buildModel(apiModel);
            Map<String, Object> data = new HashMap<>();
            data.put("importFormat", "openapi");
            // name both merge ignore
            data.put("apiOverwriteMode", apiFoxConfigObj.getOrDefault("apiOverwriteMode", "ignore"));
            data.put("data", openApi);
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
        }).build();
    }

}