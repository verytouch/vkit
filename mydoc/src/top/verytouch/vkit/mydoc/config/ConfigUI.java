package top.verytouch.vkit.mydoc.config;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import top.verytouch.vkit.mydoc.constant.ConfigVariable;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 配置持久化
 *
 * @author verytouch
 * @since 2024-09
 */
public class ConfigUI {

    private final Project project;
    private final FileChooserDescriptor folderDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);

    private JBTextField docName;
    private JBTextField contextPath;
    private JBTextField host;
    private JBTextField headers;
    private TextFieldWithBrowseButton templateDir;
    private TextFieldWithBrowseButton outputDir;
    private JCheckBox openOutputDir;
    private JCheckBox showExample;
    private JCheckBox showRequired;
    private JCheckBox showApiDesc;

    private JBTextField apiFoxProject;
    private JBTextField apiFoxToken;
    private JBTextField apiFoxOverwriteMode;
    private JBTextField apiFoxFolder;

    public ConfigUI(Project project) {
        this.project = project;

    }

    public JComponent create() {
        this.docName = new JBTextField();
        this.contextPath = new JBTextField();
        this.host = new JBTextField();
        this.headers = new JBTextField();
        this.templateDir = new TextFieldWithBrowseButton();
        this.templateDir.addBrowseFolderListener("请选择模版文件目录", "", this.project, folderDescriptor);
        this.outputDir = new TextFieldWithBrowseButton();
        this.outputDir.addBrowseFolderListener("请选择文档输出目录", "", this.project, folderDescriptor);
        this.openOutputDir = new JCheckBox("生成文档后自动打开");
        this.apiFoxProject = new JBTextField();
        this.apiFoxToken = new JBTextField();
        this.apiFoxOverwriteMode = new JBTextField();
        this.apiFoxFolder = new JBTextField();

        JPanel optional = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.showExample = new JCheckBox("请求示例");
        optional.add(this.showExample);
        this.showRequired = new JCheckBox("必填说明");
        optional.add(this.showRequired);
        this.showApiDesc = new JCheckBox("接口描述");
        optional.add(this.showApiDesc);

        String variableTip = Stream.of(ConfigVariable.values())
                .map(variable -> variable.getVariable() + "=" + variable.getRemark())
                .collect(Collectors.joining(", "));
        int verticalGap = 8;
        return FormBuilder.createFormBuilder()
                .setVerticalGap(verticalGap)
                .addComponent(new TitledSeparator("File Settings"))
                .addLabeledComponent("DocName", this.docName)
                .addLabeledComponent("Host", this.host)
                .addLabeledComponent("ContextPath", this.contextPath)
                .addTooltip(variableTip)
                .addLabeledComponent("TemplateDir", this.templateDir)
                .addTooltip("TemplateDir非空时使用该目录下的模板文件：html.ftl、markdown.ftl、word.ftl、word.docx")
                .addLabeledComponent("OutputDir", this.outputDir)
                .addComponentToRightColumn(this.openOutputDir)
                .addLabeledComponent("Headers", this.headers)
                .addTooltip("Header之间用;分隔，键值之间用:分隔")
                .addLabeledComponent("Optional", optional)
                .addVerticalGap(verticalGap)
                .addComponent(new TitledSeparator("ApiFox Settings"))
                .addLabeledComponent("Project", this.apiFoxProject)
                .addLabeledComponent("Token", this.apiFoxToken)
                .addLabeledComponent("OverwriteMode", this.apiFoxOverwriteMode)
                .addLabeledComponent("Folder", this.apiFoxFolder)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public ConfigStorage getData() {
        ConfigStorage value = new ConfigStorage();
        value.setDocName(this.docName.getText().trim());
        value.setApiServer(this.host.getText().trim());
        value.setContextPath(this.contextPath.getText().trim());
        value.setHeaders(this.headers.getText().trim());
        value.setApiFoxProject(this.apiFoxProject.getText().trim());
        value.setApiFoxToken(this.apiFoxToken.getText().trim());
        value.setApiFoxOverwriteMode(this.apiFoxOverwriteMode.getText().trim());
        value.setApiFoxFolder(this.apiFoxFolder.getText().trim());
        value.setTemplateDir(this.templateDir.getText().trim());
        value.setOutputDir(this.outputDir.getText().trim());
        value.setOpenOutDir(this.openOutputDir.isSelected());
        value.setShowExample(this.showExample.isSelected());
        value.setShowRequired(this.showRequired.isSelected());
        value.setShowApiDesc(this.showApiDesc.isSelected());
        return value;
    }

    public void updateUI(ConfigStorage value) {
        this.docName.setText(value.getDocName());
        this.host.setText(value.getApiServer());
        this.contextPath.setText(value.getContextPath());
        this.headers.setText(value.getHeaders());
        this.apiFoxProject.setText(value.getApiFoxProject());
        this.apiFoxToken.setText(value.getApiFoxToken());
        this.apiFoxOverwriteMode.setText(value.getApiFoxOverwriteMode());
        this.apiFoxFolder.setText(value.getApiFoxFolder());
        this.templateDir.setText(value.getTemplateDir());
        this.outputDir.setText(value.getOutputDir());
        this.openOutputDir.setSelected(value.isOpenOutDir());
        this.showExample.setSelected(value.isShowExample());
        this.showRequired.setSelected(value.isShowRequired());
        this.showApiDesc.setSelected(value.isShowApiDesc());
    }

}
