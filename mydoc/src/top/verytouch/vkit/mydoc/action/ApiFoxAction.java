package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.ApiFoxBuilder;
import top.verytouch.vkit.mydoc.builder.BuilderTask;

/**
 * 同步到ApiFox
 *
 * @author verytouch
 * @since 2023-11
 */
public class ApiFoxAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(new ApiFoxBuilder(event));
    }

}
