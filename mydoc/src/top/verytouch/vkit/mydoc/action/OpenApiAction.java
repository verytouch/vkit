package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.BuilderTask;
import top.verytouch.vkit.mydoc.builder.clipboard.OpenApiDocBuilder;

/**
 * 复制为openapi json
 *
 * @author verytouch
 * @since 2023-11
 */
public class OpenApiAction extends AbstractMyAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(new OpenApiDocBuilder(event));
    }

}
