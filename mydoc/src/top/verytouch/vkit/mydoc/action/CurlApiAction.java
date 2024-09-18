package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.BuilderTask;
import top.verytouch.vkit.mydoc.builder.clipboard.CurlDocBuilder;

/**
 * 复制为curl
 *
 * @author verytouch
 * @since 2023-11
 */
public class CurlApiAction extends AbstractMyAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(new CurlDocBuilder(event));
    }

}
