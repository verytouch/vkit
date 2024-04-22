package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.BuilderTask;
import top.verytouch.vkit.mydoc.builder.VoidDocBuilder;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.util.BuilderUtil;
import top.verytouch.vkit.mydoc.util.JsonUtil;

/**
 * 复制为Json
 *
 * @author verytouch
 * @since 2021-11
 */
public class JsonModelAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(new VoidDocBuilder(event, DocType.JSON, apiModel -> {
            String json = JsonUtil.toJson(apiModel);
            BuilderUtil.copyToClipboard(json);
        }));
    }

}
