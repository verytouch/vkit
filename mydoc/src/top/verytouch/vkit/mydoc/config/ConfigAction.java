package top.verytouch.vkit.mydoc.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * 配置面板
 *
 * @author verytouch
 * @since 2021-12
 */
public class ConfigAction implements Configurable {

    private final Project project;
    private final ConfigUI configUI;


    public ConfigAction(Project project) {
        this.project = project;
        this.configUI = new ConfigUI(project);
    }

    @Override
    @Nls(capitalization = Nls.Capitalization.Title)
    public String getDisplayName() {
        return "MyDoc";
    }

    @Override
    @Nullable
    public JComponent createComponent() {
        return this.configUI.create();
    }

    @Override
    public boolean isModified() {
        ConfigStorage config = this.project.getService(ConfigStorage.class);
        ConfigStorage data = this.configUI.getData();
        return !Objects.equals(config, data);
    }

    @Override
    public void apply() {
        ConfigStorage config = this.project.getService(ConfigStorage.class);
        ConfigStorage data = this.configUI.getData();
        XmlSerializerUtil.copyBean(data, config);
    }

    @Override
    public void reset() {
        ConfigStorage config = this.project.getService(ConfigStorage.class);
        this.configUI.updateUI(config);
    }

}
