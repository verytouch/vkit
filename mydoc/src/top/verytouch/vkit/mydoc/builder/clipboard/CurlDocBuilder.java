package top.verytouch.vkit.mydoc.builder.clipboard;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.config.ConfigStorage;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiModel;
import top.verytouch.vkit.mydoc.model.ApiOperation;

import java.util.stream.Collectors;

/**
 * 复制为curl
 *
 * @author verytouch
 * @since 2024-09
 */
public class CurlDocBuilder extends ClipboardDocBuilder {

    private static final String WRAP = " \\\n";

    public CurlDocBuilder(AnActionEvent event) {
        super(event, DocType.CURL);
    }

    @Override
    public String buildText(ApiModel apiModel) {
        ConfigStorage config = apiModel.getConfig();
        String headers = config.realHeaders().entrySet().stream()
                .map(entry -> "     --header '" + entry.getKey() + ": " + entry.getValue() + "'")
                .collect(Collectors.joining(WRAP));
        String header = StringUtils.isNotBlank(headers) ? WRAP + headers : "";
        return apiModel.getData().stream()
                .flatMap(g -> g.getOperationList().stream().peek(a -> a.setPath(config.getApiServer() + config.getContextPath() + g.getPath() + a.getPath())))
                .map(operation -> buildOperation(operation, header))
                .collect(Collectors.joining("\n\n\n"));
    }

    private String buildOperation(ApiOperation operation, String headers) {
        String method = operation.getMethod().split(",")[0].toUpperCase();
        String query = "";
        if (CollectionUtils.isNotEmpty(operation.getRequestParam())) {
            query = operation.getRequestParam().stream()
                    .map(field -> field.getName() + "=")
                    .collect(Collectors.joining("&", "?", ""));
        }
        String body = "";
        if (CollectionUtils.isNotEmpty(operation.getRequestFile())) {
            body = operation.getRequestFile().stream()
                    .map(field -> "     --form '" + field.getName() + "=@\"\"'")
                    .collect(Collectors.joining(WRAP, WRAP, ""));
            headers = WRAP + "     --header 'Content-Type: multipart/form-data'" + headers;
        } else if (CollectionUtils.isNotEmpty(operation.getRequestBody())) {
            body = WRAP + "     --data-raw '" + operation.getRequestBodyExample() + "'";
            headers = WRAP + "     --header 'Content-Type: Application/json'" + headers;
        }
        return "# " + operation.getName() + "\n"
                + "curl --location --request " + method + " '" + operation.getPath() + query + "'"
                + headers
                + body;
    }
}
