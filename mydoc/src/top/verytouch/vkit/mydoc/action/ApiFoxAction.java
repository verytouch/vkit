package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.http.ApiFoxDocBuilder;
import top.verytouch.vkit.mydoc.builder.BuilderTask;

/**
 * 同步到ApiFox
 *
 * @author verytouch
 * @since 2023-11
 */
public class ApiFoxAction extends AbstractMyAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(new ApiFoxDocBuilder(event));
    }

}
