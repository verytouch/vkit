package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.BuilderTask;
import top.verytouch.vkit.mydoc.builder.file.FreemarkerDocBuilder;
import top.verytouch.vkit.mydoc.constant.DocType;

/**
 * 导出为Markdown
 *
 * @author verytouch
 * @since 2021-11
 */
public class MarkdownAction extends AbstractMyAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(new FreemarkerDocBuilder(event, DocType.MARK_DOWN));
    }

}
