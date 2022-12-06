package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.builder.WordBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * 导出为word
 *
 * @author verytouch
 * @since 2021-11
 */
public class WordAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new WordBuilder(event).build();
    }
}
