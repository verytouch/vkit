package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.BuilderTask;
import top.verytouch.vkit.mydoc.builder.VoidDocBuilder;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiField;
import top.verytouch.vkit.mydoc.model.ApiModel;
import top.verytouch.vkit.mydoc.model.ApiOperation;
import top.verytouch.vkit.mydoc.util.BuilderUtil;

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
        BuilderTask.start(new VoidDocBuilder(event, DocType.IDEA_HTTP, apiModel -> {
            String text = buildIdeaHttp(apiModel);
            BuilderUtil.copyToClipboard(text);
        }));
    }

    private String buildIdeaHttp(ApiModel apiModel) {
        if (apiModel == null || CollectionUtils.isEmpty(apiModel.getData())) {
            return "";
        }
        String host = apiModel.getConfig().getApiServer();
        String headers = apiModel.getConfig().realHeaders().entrySet().stream()
                .map(h -> h.getKey() + ": " + h.getValue())
                .collect(Collectors.joining("\n"));
        String header = StringUtils.isNotBlank(headers) ? "\n" + headers : "";
        return apiModel.getData().stream()
                .flatMap(g -> g.getOperationList().stream().peek(a -> a.setPath(host + g.getPath() + a.getPath())))
                .map(operation -> buildOperation(operation, header))
                .collect(Collectors.joining("\n\n\n"));
    }

    private String buildOperation(ApiOperation apiOperation, String headers) {
        StringBuilder content = new StringBuilder();
        content.append("### ").append(apiOperation.getName())
                .append("\n").append(apiOperation.getMethod()).append(" ").append(apiOperation.getPath());
        if (CollectionUtils.isNotEmpty(apiOperation.getRequestParam())) {
            String params = apiOperation.getRequestParam().stream()
                    .map(p -> p.getName() + "=")
                    .collect(Collectors.joining("&", "?", ""));
            content.append(params);
        }
        content.append(headers);
        if (CollectionUtils.isNotEmpty(apiOperation.getRequestFile())) {
            content.append("\nContent-Type: multipart/form-data; boundary=WebAppBoundary");
            for (ApiField file : apiOperation.getRequestFile()) {
                content.append("\n\n--WebAppBoundary")
                        .append("\nContent-Disposition: form-data; name=\"").append(file.getName()).append("\"; filename=\"file.txt\"")
                        .append("\n\n< D:\\\\file.txt");
            }
            content.append("\n--WebAppBoundary--");
        } else if (StringUtils.isNotBlank(apiOperation.getRequestBodyExample())) {
            content.append("\nContent-Type: application/json")
                    .append("\n\n").append(apiOperation.getRequestBodyExample());
        }
        return content.toString();
    }

}
