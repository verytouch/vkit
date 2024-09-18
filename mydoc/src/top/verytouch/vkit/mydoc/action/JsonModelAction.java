package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.BuilderTask;
import top.verytouch.vkit.mydoc.builder.clipboard.ClipboardDocBuilder;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.util.JsonUtil;

/**
 * 复制为Json
 *
 * @author verytouch
 * @since 2021-11
 */
public class JsonModelAction extends AbstractMyAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(ClipboardDocBuilder.of(event, DocType.JSON, JsonUtil::toJson));
    }

}
