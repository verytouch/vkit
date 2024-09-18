package top.verytouch.vkit.mydoc.builder.clipboard;

import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiModel;
import top.verytouch.vkit.mydoc.util.OpenApiUtil;

/**
 * 复制为open-api
 *
 * @author verytouch
 * @since 2024-09
 */
public class OpenApiDocBuilder extends ClipboardDocBuilder {

    public OpenApiDocBuilder(AnActionEvent event) {
        super(event, DocType.OPEN_API);
    }

    @Override
    protected String buildText(ApiModel model) {
        return OpenApiUtil.buildModel(model).toJson();
    }
}
