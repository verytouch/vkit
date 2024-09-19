package top.verytouch.vkit.mydoc.action.group;

import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * 只在选择了java文件时显示菜单
 *
 * @author verytouch
 * @since 2021-12-5
 */
public abstract class JavaActionGroup extends DefaultActionGroup {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        PsiFile psiFile = event.getData(PlatformDataKeys.PSI_FILE);
        if (isJavaFile(psiFile) || isJavaDirectory(psiFile)) {
            event.getPresentation().setVisible(true);
            return;
        }
        Object[] data = event.getData(PlatformDataKeys.SELECTED_ITEMS);
        if (data == null || data.length == 0) {
            event.getPresentation().setVisible(false);
            return;
        }
        boolean javaFileSelected = Arrays.stream(data).anyMatch(e -> isJavaFile(e) || isJavaDirectory(e));
        event.getPresentation().setVisible(javaFileSelected);
    }

    public static boolean isJavaFile(Object data) {
        if (data == null) {
            return false;
        }
        return data instanceof PsiJavaFile ||
                data instanceof PsiClass;
    }

    public static boolean isJavaDirectory(Object data) {
        if (data == null) {
            return false;
        }
        return data instanceof PsiDirectoryNode;
    }

}
