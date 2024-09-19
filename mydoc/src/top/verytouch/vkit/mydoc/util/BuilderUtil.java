package top.verytouch.vkit.mydoc.util;

import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.builder.ApiBuilder;
import top.verytouch.vkit.mydoc.config.ConfigStorage;
import top.verytouch.vkit.mydoc.constant.SpecialClassNames;
import top.verytouch.vkit.mydoc.model.ApiGroup;
import top.verytouch.vkit.mydoc.model.ApiModel;

import java.util.*;

import static top.verytouch.vkit.mydoc.constant.SpecialClassNames.CONTROLLER;
import static top.verytouch.vkit.mydoc.constant.SpecialClassNames.REST_CONTROLLER;
import static top.verytouch.vkit.mydoc.util.AnnotationUtil.hasAnyAnnotation;

/**
 * BuilderUtil
 *
 * @author verytouch
 * @since 2022-11
 */
public class BuilderUtil {

    public static final int CURRENT_IN_EDITOR = 1;
    public static final int SELECTED_IN_TREE = 2;
    public static final int ALL_IN_PROJECT = 3;

    /**
     * 生成文档模型
     *
     * @param event event
     * @param scope 1编辑器中打开的类  2左侧树中选中的包  3当前项目所有controller
     */
    public static ApiModel buildModel(AnActionEvent event, int scope) {
        // 范围
        List<PsiClass> classes = null;
        String selectedMethod = null;
        switch (scope) {
            case CURRENT_IN_EDITOR:
                classes = getCurrentJavaClass(event);
                selectedMethod = getSelectedMethod(event);
                break;
            case SELECTED_IN_TREE:
                classes = getSelectJavaClass(event);
                break;
            case ALL_IN_PROJECT:
                classes = getAllControllerClass(event);
                break;
        }
        // 接口
        ConfigStorage config = getConfig(event);
        ApiBuilder builder = new ApiBuilder(config);
        List<ApiGroup> data = buildData(builder, classes, selectedMethod);
        // 名称
        String name;
        if (StringUtils.isNotBlank(config.getDocName())) {
            name = config.getDocName();
        } else if (data.size() == 1) {
            name = data.get(0).getName();
        } else if (event.getData(LangDataKeys.MODULE) != null) {
            name = Objects.requireNonNull(event.getData(LangDataKeys.MODULE)).getName();
        } else {
            name = "MyDoc";
        }
        // 模板数据
        ApiModel model = new ApiModel();
        model.setName(name);
        model.setConfig(config);
        model.setData(data);
        return model;
    }

    /**
     * 生成文档数据
     *
     * @param builder         builder
     * @param toBuildList     选中的类
     * @param specifiedMethod 选中的方法
     */
    public static List<ApiGroup> buildData(ApiBuilder builder, List<PsiClass> toBuildList, String specifiedMethod) {
        List<ApiGroup> data = new LinkedList<>();
        if (CollectionUtils.isEmpty(toBuildList)) {
            return data;
        }
        for (PsiClass psiClass : toBuildList) {
            ApiGroup apiGroup = builder.build(psiClass, specifiedMethod);
            if (apiGroup == null || CollectionUtils.isEmpty(apiGroup.getOperationList())) {
                continue;
            }
            data.add(apiGroup);
        }
        return data;
    }

    /**
     * 获取配置
     */
    public static ConfigStorage getConfig(AnActionEvent event) {
        ConfigStorage config = event.getProject().getService(ConfigStorage.class);
        config.setContextPath(config.realContextPath(event));
        return config;
    }

    /**
     * 判断是否在编辑器中触发
     */
    public static boolean isInEditor(AnActionEvent event) {
        return event.getData(LangDataKeys.PSI_FILE) != null;
    }

    /**
     * 获取编辑器中当前类
     */
    public static List<PsiClass> getCurrentJavaClass(AnActionEvent event) {
        PsiJavaFile psiJavaFile = (PsiJavaFile) event.getData(LangDataKeys.PSI_FILE);
        if (psiJavaFile == null) {
            return null;
        }
        List<PsiClass> psiClasses = new ArrayList<>();
        psiClasses.add(psiJavaFile.getClasses()[0]);
        return psiClasses;
    }

    /**
     * 获取编辑器中当前类鼠标选中的方法名称
     */
    public static String getSelectedMethod(AnActionEvent event) {
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        String selectedMethod = null;
        if (editor != null && editor.getSelectionModel().hasSelection()) {
            selectedMethod = editor.getSelectionModel().getSelectedText();
        }
        return selectedMethod;
    }

    /**
     * 获取左侧书中选中的所有类
     */
    public static List<PsiClass> getSelectJavaClass(AnActionEvent event) {
        Object[] data = event.getData(PlatformDataKeys.SELECTED_ITEMS);
        List<PsiClass> javaClasses = new LinkedList<>();
        if (data == null) {
            return javaClasses;
        }
        for (Object datum : data) {
            addAllClass(javaClasses, datum);
        }
        return javaClasses;
    }

    /**
     * 获取当前项目所有Controller类
     */
    public static List<PsiClass> getAllControllerClass(AnActionEvent event) {
        String restController = SpecialClassNames.REST_CONTROLLER.substring(SpecialClassNames.REST_CONTROLLER.lastIndexOf(".") + 1);
        String controller = SpecialClassNames.CONTROLLER.substring(SpecialClassNames.CONTROLLER.lastIndexOf(".") + 1);

        List<PsiClass> javaClasses = new LinkedList<>();
        Project project = event.getProject();
        assert project != null;
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            Collection<PsiAnnotation> annotations = JavaAnnotationIndex.getInstance().getAnnotations(restController, project, module.getModuleScope());
            annotations.addAll(JavaAnnotationIndex.getInstance().getAnnotations(controller, project, module.getModuleScope()));
            for (PsiAnnotation psiAnnotation : annotations) {
                PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
                PsiElement psiElement = psiModifierList.getParent();
                addAllClass(javaClasses, psiElement);
            }
        }
        return javaClasses;
    }

    /**
     * 递归添加Controller
     */
    private static void addAllClass(List<PsiClass> javaClasses, Object selectedItem) {
        if (selectedItem instanceof PsiClass) {
            if (hasAnyAnnotation((PsiClass) selectedItem, REST_CONTROLLER, CONTROLLER)) {
                javaClasses.add((PsiClass) selectedItem);
            }
        } else if (selectedItem instanceof PsiJavaFile) {
            PsiClass[] psiClasses = ((PsiJavaFile) selectedItem).getClasses();
            if (psiClasses.length > 0) {
                if (hasAnyAnnotation(psiClasses[0], REST_CONTROLLER, CONTROLLER)) {
                    javaClasses.add(psiClasses[0]);
                }
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
