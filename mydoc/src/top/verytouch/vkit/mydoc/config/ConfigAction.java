package top.verytouch.vkit.mydoc.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import top.verytouch.vkit.mydoc.constant.ConfigVariable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 配置面板
 *
 * @author verytouch
 * @since 2021-12
 */
public class ConfigAction implements Configurable {

    private final Project project;

    private JTextField docName;

    private JTextField contextPath;

    private JTextField host;

    private JTextArea headers;

    private JTextArea apiFox;

    private JTextField templateDir;

    private JCheckBox showExample;

    private JCheckBox showRequired;

    private JCheckBox showApiDesc;

    public ConfigAction(Project project) {
        this.project = project;
    }

    @Override
    @Nls(capitalization = Nls.Capitalization.Title)
    public String getDisplayName() {
        return "MyDoc";
    }

    @Override
    @Nullable
    public JComponent createComponent() {
        List<String> tips = new ArrayList<>();
        tips.add(Stream.of(ConfigVariable.values())
                .map(variable -> variable.getVariable() + "=" + variable.getRemark())
                .collect(Collectors.joining(", ")));
        tips.add("Header每行一个，键值之间用冒号分隔");
        tips.add("TemplateDir非空时使用该目录下的模板文件：html.ftl、markdown.ftl");

        docName = new JTextField();
        contextPath = new JTextField();
        host = new JTextField();
        headers = new JTextArea(2, 0);
        apiFox = new JTextArea(2, 0);
        templateDir = new JTextField();
        showExample = new JCheckBox("请求示例");
        showRequired = new JCheckBox("必填说明");
        showApiDesc = new JCheckBox("接口描述");

        JPanel checkBoxGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkBoxGroup.add(showExample);
        checkBoxGroup.add(showRequired);
        checkBoxGroup.add(showApiDesc);

        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        for (String tip : tips) {
            formBuilder.addTooltip(tip);
        }
        return formBuilder.addLabeledComponent("DocName", docName)
                .addLabeledComponent("Host", host)
                .addLabeledComponent("ContextPath", contextPath)
                .addLabeledComponent("TemplateDir", templateDir)
                .addLabeledComponent("Headers", headers)
                .addLabeledComponent("Optional", checkBoxGroup)
                .addLabeledComponent("Headers", apiFox)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    @Override
    public boolean isModified() {
        ConfigStorage config = ConfigStorage.getInstance(project);
        return isModified(docName, config.docName) ||
                isModified(host, config.apiServer) ||
                isModified(contextPath, config.contextPath) ||
                !headers.getText().trim().equals(config.headers) ||
                !apiFox.getText().trim().equals(config.apiFox) ||
                isModified(templateDir, config.templateDir) ||
                isModified(showExample, config.showExample) ||
                isModified(showRequired, config.showRequired) ||
                isModified(showApiDesc, config.showApiDesc);
    }

    @Override
    public void apply() {
        ConfigStorage config = ConfigStorage.getInstance(project);
        config.docName = docName.getText().trim();
        config.apiServer = host.getText().trim();
        config.contextPath = contextPath.getText().trim();
        config.headers = headers.getText().trim();
        config.apiFox = apiFox.getText().trim();
        config.templateDir = templateDir.getText().trim();
        config.showExample = showExample.isSelected();
        config.showRequired = showRequired.isSelected();
        config.showApiDesc = showApiDesc.isSelected();
    }

    @Override
    public void reset() {
        ConfigStorage config = ConfigStorage.getInstance(project);
        docName.setText(config.docName);
        host.setText(config.apiServer);
        contextPath.setText(config.contextPath);
        headers.setText(config.headers);
        apiFox.setText(config.apiFox);
        templateDir.setText(config.templateDir);
        showExample.setSelected(config.showExample);
        showRequired.setSelected(config.showRequired);
        showApiDesc.setSelected(config.showApiDesc);
    }

}
