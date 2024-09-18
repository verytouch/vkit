package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.BuilderTask;
import top.verytouch.vkit.mydoc.builder.file.PostmanDocBuilder;

/**
 * 导出为postman.collection
 *
 * @author verytouch
 * @since 2021-11
 */
public class PostmanAction extends AbstractMyAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(new PostmanDocBuilder(event));
    }

}
