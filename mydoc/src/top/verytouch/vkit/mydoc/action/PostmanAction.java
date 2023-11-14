package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.PostmanBuilder;

/**
 * 导出为postman.collection
 *
 * @author verytouch
 * @since 2021-11
 */
public class PostmanAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new PostmanBuilder(event).build();
    }

}
