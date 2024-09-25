package top.verytouch.vkit.mydoc.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.filechooser.FileSystemView;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 配置持久化
 *
 * @author verytouch
 * @since 2021-12
 */
@State(name = "top.verytouch.vkit.mydoc.config.ConfigAction", storages = @Storage("mydoc.xml"))
@Data
public class ConfigStorage implements PersistentStateComponent<ConfigStorage> {
    // common
    @Tag
    private String contextPath = "";
    @Tag
    private String apiServer = "{{gateway}}";
    @Tag
    private String headers = "Authorization:Bearer {{token}}";
    @Tag
    private String templateDir = "";
    @Tag
    private String outputDir = FileSystemView.getFileSystemView().getHomeDirectory().getPath();
    @Tag
    private boolean openOutDir = true;
    @Tag
    private boolean showExample = true;
    @Tag
    private boolean showRequired = false;
    @Tag
    private boolean showApiDesc = true;
    @Tag
    private boolean ignoreMap = true;
    @Tag
    private String docName = "";
    // apifox
    @Tag
    private String apiFoxProject = "";
    @Tag
    private String apiFoxToken = "";
    @Tag
    private String apiFoxOverwriteMode = "ignore";
    @Tag
    private String apiFoxFolder = "mydoc";

    @Override
    @Nullable
    @JsonIgnore
    public ConfigStorage getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ConfigStorage configStorage) {
        XmlSerializerUtil.copyBean(configStorage, this);
    }

    public String realContextPath(AnActionEvent event) {
        String realValue = this.getContextPath();
        for (ConfigVariable value : ConfigVariable.values()) {
            String variable = value.getVariable();
            if (realValue.contains(variable)) {
                realValue = realValue.replace(variable, value.getMapper().apply(event));
            }
        }
        return realValue;
    }

    public Map<String, String> realHeaders() {
        if (StringUtils.isBlank(this.getHeaders())) {
            return new HashMap<>();
        }
        return Stream.of(this.getHeaders().split(";"))
                .filter(StringUtils::isNotBlank)
                .map(line -> line.trim().split(":", 2))
                .filter(header -> header.length == 2)
                .collect(Collectors.toMap(header -> header[0], header -> header[1], (k1, k2) -> k1));
    }
}
