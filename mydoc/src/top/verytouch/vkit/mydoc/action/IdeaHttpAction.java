package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.OutputStreamDocBuilder;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiGroup;
import top.verytouch.vkit.mydoc.model.ApiModel;
import top.verytouch.vkit.mydoc.model.ApiOperation;
import top.verytouch.vkit.mydoc.util.JsonUtil;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * 导出为idea.http
 *
 * @author verytouch
 * @since 2021-11
 */
public class IdeaHttpAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new OutputStreamDocBuilder(event, DocType.IDEA_HTTP) {
            @Override
            protected OutputStream buildDoc(String path) throws Exception {
                FileOutputStream outputStream = new FileOutputStream(path);
                String content = buildIdeaHttp(model);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
                IOUtils.copy(inputStream, outputStream);
                return outputStream;
            }
        }.build();
    }

    private String buildIdeaHttp(ApiModel apiModel) {
        StringBuilder content = new StringBuilder();
        if (apiModel == null || CollectionUtils.isEmpty(apiModel.getData())) {
            return content.toString();
        }
        String host = apiModel.getConfig().getApiServer();
        String headers = apiModel.getConfig().realHeaders().entrySet().stream()
                .map(h -> h.getKey() + ": " + h.getValue())
                .collect(Collectors.joining("\n", "\n", ""));
        for (ApiGroup apiGroup : apiModel.getData()) {
            for (ApiOperation apiOperation : apiGroup.getOperationList()) {
                content.append("### ").append(apiOperation.getName())
                        .append("\n").append(apiOperation.getMethod()).append(" ").append(host).append(apiGroup.getPath()).append(apiOperation.getPath());
                if (CollectionUtils.isNotEmpty(apiOperation.getRequestParam())) {
                    String params = apiOperation.getRequestParam().stream()
                            .map(p -> p.getName() + "=")
                            .collect(Collectors.joining("&", "?", ""));
                    content.append(params);
                }
                content.append(headers);
                if (StringUtils.isNotBlank(apiOperation.getRequestBodyExample())) {
                    content.append("\nContent-Type: application/json")
                            .append("\n\n").append(apiOperation.getRequestBodyExample());
                }
                content.append("\n\n\n");
            }
        }
        return content.toString();
    }

}
