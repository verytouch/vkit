package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.BuilderTask;
import top.verytouch.vkit.mydoc.builder.file.WordDocDocBuilder;

/**
 * 导出为word
 *
 * @author verytouch
 * @since 2021-11
 */
public class WordAction extends AbstractMyAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(new WordDocDocBuilder(event));
    }

}
