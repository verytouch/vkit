package top.verytouch.vkit.mydoc.model;

import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.config.ConfigStorage;
import top.verytouch.vkit.mydoc.constant.ConfigVariable;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 配置
 *
 * @author verytouch
 * @since 2021-12
 */
@Data
@Accessors(chain = true)
public class ApiConfig {

    public String apiServer;

    public String contextPath;

    public String headers;

    public String apiFox;

    public String templateDir;

    public boolean showExample;

    public boolean showRequired;

    public boolean showApiDesc;

    public String docName;

    public ApiConfig(ConfigStorage storage) {
        this.apiServer = storage.apiServer;
        this.contextPath = storage.contextPath;
        this.headers = storage.headers;
        this.apiFox = storage.apiFox;
        this.templateDir = storage.templateDir;
        this.showApiDesc = storage.showApiDesc;
        this.showExample = storage.showExample;
        this.showRequired = storage.showRequired;
        this.docName = storage.docName;
    }

    public String realContextPath(AnActionEvent event) {
        String realValue = contextPath;
        for (ConfigVariable value : ConfigVariable.values()) {
            String variable = value.getVariable();
            if (realValue.contains(variable)) {
                realValue = realValue.replace(variable, value.getMapper().apply(event));
            }
        }
        return realValue;
    }

    public Map<String, String> realHeaders() {
        if (StringUtils.isBlank(headers)) {
            return new HashMap<>();
        }
        return Stream.of(headers.split("[\r\n]"))
                .filter(StringUtils::isNotBlank)
                .map(line -> line.trim().split(":", 2))
                .filter(header -> header.length == 2)
                .collect(Collectors.toMap(header -> header[0], header -> header[1], (k1, k2) -> k1));
    }
}
