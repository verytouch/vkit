package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTypesUtil;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.action.group.JavaActionGroup;
import top.verytouch.vkit.mydoc.builder.ApiBuilder;
import top.verytouch.vkit.mydoc.builder.BuilderTask;
import top.verytouch.vkit.mydoc.builder.JsonSchemaBuilder;
import top.verytouch.vkit.mydoc.builder.VoidDocBuilder;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiConfig;
import top.verytouch.vkit.mydoc.model.ApiField;
import top.verytouch.vkit.mydoc.util.BuilderUtil;
import top.verytouch.vkit.mydoc.util.JsonUtil;
import top.verytouch.vkit.mydoc.util.JsonUtil.JsonObject;
import top.verytouch.vkit.mydoc.util.NotifyUtil;

import java.util.List;

/**
 * 复制为JsonSchema
 *
 * @author verytouch
 * @since 2021-12
 */
public class JsonSchemaAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(new JsonSchemaBuilder(event));
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        PsiFile psiFile = event.getData(PlatformDataKeys.PSI_FILE);
        if (JavaActionGroup.isJavaFile(psiFile)) {
            event.getPresentation().setVisible(true);
        }
    }

}
