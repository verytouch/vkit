package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.BuilderTask;
import top.verytouch.vkit.mydoc.builder.VoidDocBuilder;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.util.BuilderUtil;
import top.verytouch.vkit.mydoc.util.JsonUtil;
import top.verytouch.vkit.mydoc.util.OpenApiUtil;

/**
 * 复制为openapi json
 *
 * @author verytouch
 * @since 2023-11
 */
public class OpenApiAction extends AbstractMyAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BuilderTask.start(new VoidDocBuilder(event, DocType.OPEN_API, apiModel -> {
            JsonUtil.JsonObject<String, Object> openApi = OpenApiUtil.buildModel(apiModel);
            BuilderUtil.copyToClipboard(openApi.toJson());
        }));
    }

}
