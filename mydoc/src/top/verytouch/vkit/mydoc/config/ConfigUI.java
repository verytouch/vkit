package top.verytouch.vkit.mydoc.config;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.util.execution.ParametersListUtil;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
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
    private ExpandableTextField headers;
    private TextFieldWithBrowseButton templateDir;
    private TextFieldWithBrowseButton outputDir;
    private JCheckBox openOutputDir;
    private JCheckBox showExample;
    private JCheckBox showRequired;
    private JCheckBox showApiDesc;
    private JCheckBox ignoreMap;

    private JBTextField apiFoxProject;
    private JBTextField apiFoxToken;
    private ButtonGroup apiFoxOverwriteMode;
    private JBTextField apiFoxFolder;

    public ConfigUI(Project project) {
        this.project = project;

    }

    public JComponent create() {
        this.docName = new JBTextField();
        this.contextPath = new JBTextField();
        this.host = new JBTextField();
        this.headers = new ExpandableTextField(ParametersListUtil.COLON_LINE_PARSER, ParametersListUtil.COLON_LINE_JOINER);
        this.headers.setColumns(0);
        this.templateDir = new TextFieldWithBrowseButton();
        this.templateDir.addBrowseFolderListener("TemplateDir", "", this.project, folderDescriptor);
        this.outputDir = new TextFieldWithBrowseButton();
        this.outputDir.addBrowseFolderListener("OutputDir", "", this.project, folderDescriptor);
        this.openOutputDir = new JCheckBox("Open the file after building");
        this.apiFoxProject = new JBTextField();
        this.apiFoxToken = new JBTextField();
        this.apiFoxFolder = new JBTextField();

        String variableTip = Stream.of(ConfigVariable.values())
                .map(variable -> variable.getVariable() + "=" + variable.getRemark())
                .collect(Collectors.joining(", "));
        int verticalGap = 8;
        return FormBuilder.createFormBuilder()
                .setVerticalGap(verticalGap)
                .addComponent(new TitledSeparator("File Settings"))
                .addLabeledComponent("DocName", this.docName)
                .addTooltip("DocName > ControllerName > ModuleName > 'MyDoc'")
                .addLabeledComponent("Host", this.host)
                .addLabeledComponent("ContextPath", this.contextPath)
                .addTooltip(variableTip)
                .addLabeledComponent("TemplateDir", this.templateDir)
                .addTooltip("Using these files in template dir：html.ftl、markdown.ftl、word.ftl、word.docx")
                .addLabeledComponent("OutputDir", this.outputDir)
                .addComponentToRightColumn(this.openOutputDir)
                .addLabeledComponent("Headers", this.headers)
                .addTooltip("HeaderKey:HeaderValue")
                .addLabeledComponent("Optional", this.createFieldOptions())
                .addVerticalGap(verticalGap)
                .addComponent(new TitledSeparator("ApiFox Settings"))
                .addLabeledComponent("Project", this.apiFoxProject)
                .addLabeledComponent("Token", this.apiFoxToken)
                .addLabeledComponent("OverwriteMode", this.createMergeOptions())
                .addLabeledComponent("Folder", this.apiFoxFolder)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    private JPanel createMergeOptions() {
        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JBRadioButton[] buttons = new JBRadioButton[]{
                new JBRadioButton("Ignore"),
                new JBRadioButton("Merge"),
                new JBRadioButton("Name"),
                new JBRadioButton("Both")
        };
        this.apiFoxOverwriteMode = new ButtonGroup();
        for (JBRadioButton button : buttons) {
            options.add(button);
            this.apiFoxOverwriteMode.add(button);
        }
        return options;
    }

    private JPanel createFieldOptions() {
        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.showExample = new JCheckBox("Show Example");
        this.showRequired = new JCheckBox("Show Required");
        this.showApiDesc = new JCheckBox("Show desc");
        this.ignoreMap = new JCheckBox("Ignore Map");
        options.add(this.showExample);
        options.add(this.showRequired);
        options.add(this.showApiDesc);
        options.add(this.ignoreMap);
        return options;
    }

    public ConfigStorage getData() {
        ConfigStorage value = new ConfigStorage();
        value.setDocName(this.docName.getText().trim());
        value.setApiServer(this.host.getText().trim());
        value.setContextPath(this.contextPath.getText().trim());
        value.setHeaders(this.headers.getText().trim());
        value.setApiFoxProject(this.apiFoxProject.getText().trim());
        value.setApiFoxToken(this.apiFoxToken.getText().trim());
        value.setApiFoxOverwriteMode(this.getSelected(this.apiFoxOverwriteMode));
        value.setApiFoxFolder(this.apiFoxFolder.getText().trim());
        value.setTemplateDir(this.templateDir.getText().trim());
        value.setOutputDir(this.outputDir.getText().trim());
        value.setOpenOutDir(this.openOutputDir.isSelected());
        value.setShowExample(this.showExample.isSelected());
        value.setShowRequired(this.showRequired.isSelected());
        value.setShowApiDesc(this.showApiDesc.isSelected());
        value.setIgnoreMap(this.ignoreMap.isSelected());
        return value;
    }

    public void updateUI(ConfigStorage value) {
        this.docName.setText(value.getDocName());
        this.host.setText(value.getApiServer());
        this.contextPath.setText(value.getContextPath());
        this.headers.setText(value.getHeaders());
        this.apiFoxProject.setText(value.getApiFoxProject());
        this.apiFoxToken.setText(value.getApiFoxToken());
        this.setSelected(this.apiFoxOverwriteMode, value.getApiFoxOverwriteMode());
        this.apiFoxFolder.setText(value.getApiFoxFolder());
        this.templateDir.setText(value.getTemplateDir());
        this.outputDir.setText(value.getOutputDir());
        this.openOutputDir.setSelected(value.isOpenOutDir());
        this.showExample.setSelected(value.isShowExample());
        this.showRequired.setSelected(value.isShowRequired());
        this.showApiDesc.setSelected(value.isShowApiDesc());
        this.ignoreMap.setSelected(value.isIgnoreMap());
    }

    private void setSelected(ButtonGroup buttonGroup, String selection) {
        Enumeration<AbstractButton> elements = buttonGroup.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton button = elements.nextElement();
            if (button.getText().toLowerCase().endsWith(selection)) {
                button.setSelected(true);
                break;
            }
        }
    }

    private String getSelected(ButtonGroup buttonGroup) {
        Enumeration<AbstractButton> elements = buttonGroup.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton button = elements.nextElement();
            if (button.isSelected()) {
                return button.getText().toLowerCase();
            }
        }
        return "";
    }

}
