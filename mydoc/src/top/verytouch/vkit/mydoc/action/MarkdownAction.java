package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.builder.BuilderTask;
import top.verytouch.vkit.mydoc.builder.FreemarkerBuilder;
import top.verytouch.vkit.mydoc.constant.DocType;
import org.jetbrains.annotations.NotNull;

/**
 * 导出为Markdown
 *
 * @author verytouch
 * @since 2021-11
 */
public class MarkdownAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(new FreemarkerBuilder(event, DocType.MARK_DOWN));
    }

}
