package top.verytouch.vkit.mydoc.builder.clipboard;

import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiModel;
import top.verytouch.vkit.mydoc.util.JsonUtil;

/**
 * 复制为json
 *
 * @author verytouch
 * @since 2024-09
 */
public class JsonModelDocBuilder extends ClipboardDocBuilder {

    public JsonModelDocBuilder(AnActionEvent event) {
        super(event, DocType.JSON);
    }

    @Override
    protected String buildText(ApiModel model) {
        return JsonUtil.toJson(model);
    }
}
