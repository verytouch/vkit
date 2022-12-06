package top.verytouch.vkit.mydoc.builder;

import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.config.ConfigStorage;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiConfig;
import top.verytouch.vkit.mydoc.model.ApiGroup;
import top.verytouch.vkit.mydoc.model.ApiModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文档生成
 *
 * @author verytouch
 * @since 2021-12
 */
public abstract class DocBuilder {

    protected final AnActionEvent event;
    protected final DocType docType;
    protected ApiModel model;

    protected DocBuilder(AnActionEvent event, DocType docType) {
        this.event = event;
        this.docType = docType;
    }

    /**
     * 执行构建任务
     * dea下方出现进度条
     * buildApi -> buildDoc
     */
    public final void build() {
        ProgressManager.getInstance().run(new BuilderTask(this));
    }

    /**
     * 生成接口
     */
    protected void buildApi() {
        // 配置
        ApiConfig config = new ApiConfig(ConfigStorage.getInstance(event.getProject()));
        config.setContextPath(config.realContextPath(event));
        // 接口
        List<ApiGroup> data = new ArrayList<>();
        ApiBuilder apiBuilder = new ApiBuilder(config);
        PsiJavaFile psiJavaFile = (PsiJavaFile) event.getData(LangDataKeys.PSI_FILE);
        if (psiJavaFile != null) {
            Editor editor = event.getData(PlatformDataKeys.EDITOR);
            String selectedMethod = null;
            if (editor != null && editor.getSelectionModel().hasSelection()) {
                selectedMethod = editor.getSelectionModel().getSelectedText();
            }
            ApiGroup apiGroup = apiBuilder.build(psiJavaFile.getClasses()[0], selectedMethod);
            if (apiGroup != null) {
                data.add(apiGroup);
            }
        } else {
            List<PsiClass> psiClassList = getSelectJavaClass();
            for (PsiClass psiClass : psiClassList) {
                ApiGroup apiGroup = apiBuilder.build(psiClass, null);
                if (apiGroup == null || CollectionUtils.isEmpty(apiGroup.getOperationList())) {
                    continue;
                }
                data.add(apiGroup);
            }
        }
        // 名称
        String name;
        if (StringUtils.isNotBlank(config.docName)) {
            name = config.docName;
        } else if (data.size() == 1) {
            name = data.get(0).getName();
        } else if (event.getData(LangDataKeys.MODULE) != null) {
            name = Objects.requireNonNull(event.getData(LangDataKeys.MODULE)).getName();
        } else {
            name = "接口文档";
        }
        // 模板数据
        ApiModel model = new ApiModel();
        model.setName(name);
        model.setConfig(config);
        model.setData(data);
        this.model = model;
    }

    private List<PsiClass> getSelectJavaClass() {
        Object[] data = event.getData(PlatformDataKeys.SELECTED_ITEMS);
        List<PsiClass> javaClasses = new ArrayList<>();
        if (data == null) {
            return javaClasses;
        }
        for (Object datum : data) {
            addAllClass(javaClasses, datum);
        }
        return javaClasses;
    }

    private void addAllClass(List<PsiClass> javaClasses, Object selectedItem) {
        if (selectedItem instanceof PsiClass) {
            javaClasses.add((PsiClass) selectedItem);
        } else if (selectedItem instanceof PsiJavaFile) {
            PsiClass[] psiClasses = ((PsiJavaFile) selectedItem).getClasses();
            if (psiClasses.length > 0) {
                javaClasses.add(psiClasses[0]);
            }
        } else if (selectedItem instanceof PsiJavaDirectoryImpl) {
            PsiElement[] psiElements = ((PsiJavaDirectoryImpl) selectedItem).getChildren();
            for (PsiElement child : psiElements) {
                addAllClass(javaClasses, child);
            }
        } else if (selectedItem instanceof PsiDirectoryNode) {
            PsiElement[] psiElements = ((PsiDirectoryNode) selectedItem).getValue().getChildren();
            for (PsiElement child : psiElements) {
                addAllClass(javaClasses, child);
            }
        }
    }

}
