package top.verytouch.vkit.mydoc.builder.clipboard;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.constant.ClassKind;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiField;
import top.verytouch.vkit.mydoc.model.ApiModel;
import top.verytouch.vkit.mydoc.model.ApiOperation;

import java.util.stream.Collectors;

/**
 * 复制为idea-http
 *
 * @author verytouch
 * @since 2024-09
 */
public class IdeaHttpDocBuilder extends ClipboardDocBuilder {

    public IdeaHttpDocBuilder(AnActionEvent event) {
        super(event, DocType.IDEA_HTTP);
    }

    @Override
    public String buildText(ApiModel apiModel) {
        if (apiModel == null || CollectionUtils.isEmpty(apiModel.getData())) {
            return null;
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
                        .append("\nContent-Disposition: form-data; name=\"").append(file.getName()).append("\"");
                if (file.getClassKind() == ClassKind.FILE) {
                    content.append("; filename=\"file.txt\"")
                            .append("\n\n< D:\\\\file.txt");
                } else {
                    content.append("\n\n").append(file.getMock());
                }
            }
            content.append("\n--WebAppBoundary--");
        } else if (StringUtils.isNotBlank(apiOperation.getRequestBodyExample())) {
            content.append("\nContent-Type: application/json")
                    .append("\n\n").append(apiOperation.getRequestBodyExample());
        }
        return content.toString();
    }
}
