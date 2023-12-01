package top.verytouch.vkit.mydoc.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import top.verytouch.vkit.mydoc.constant.ConfigVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 配置持久化
 *
 * @author verytouch
 * @since 2021-12
 */
@State(name = "top.verytouch.vkit.mydoc.config.ConfigAction", storages = @Storage("mydoc.xml"))
public class ConfigStorage implements PersistentStateComponent<ConfigStorage> {

    public String contextPath = "";

    public String apiServer = "{{gateway}}";

    public String headers = "Authorization:Bearer {{token}}";

    public String apiFox = "{\"project\":\"\", \"token\":\"\", \"apiOverwriteMode\":\"ignore\", \"folder\":\"mydoc\", \"status\":\"\"}";

    public String templateDir = "";

    public boolean showExample = true;

    public boolean showRequired = false;

    public boolean showApiDesc = true;

    public String docName = "";

    @Override
    @Nullable
    public ConfigStorage getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ConfigStorage configStorage) {
        XmlSerializerUtil.copyBean(configStorage, this);
    }

    public static ConfigStorage getInstance(Project project) {
        return ServiceManager.getService(project, ConfigStorage.class);
    }

}
