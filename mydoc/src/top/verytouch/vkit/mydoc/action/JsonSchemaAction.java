package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.action.group.JavaActionGroup;
import top.verytouch.vkit.mydoc.builder.BuilderTask;
import top.verytouch.vkit.mydoc.builder.clipboard.JsonSchemaDocBuilder;

/**
 * 复制为JsonSchema
 *
 * @author verytouch
 * @since 2021-12
 */
public class JsonSchemaAction extends AbstractMyAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(new JsonSchemaDocBuilder(event));
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        PsiFile psiFile = event.getData(PlatformDataKeys.PSI_FILE);
        if (JavaActionGroup.isJavaFile(psiFile)) {
            event.getPresentation().setVisible(true);
        }
    }

}
