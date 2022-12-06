package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.builder.FreemarkerBuilder;
import top.verytouch.vkit.mydoc.constant.DocType;
import org.jetbrains.annotations.NotNull;

/**
 * 导出为Html
 *
 * @author verytouch
 * @since 2021-11
 */
public class HtmlAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new FreemarkerBuilder(event, DocType.HTML).build();
    }

}
