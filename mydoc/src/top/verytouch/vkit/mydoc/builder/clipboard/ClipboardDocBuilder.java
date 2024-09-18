package top.verytouch.vkit.mydoc.builder.clipboard;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.builder.DocBuilder;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiModel;
import top.verytouch.vkit.mydoc.util.BuilderUtil;

import java.util.function.Function;

/**
 * 复制到剪贴板
 *
 * @author verytouch
 * @since 2024-09
 */
public abstract class ClipboardDocBuilder extends DocBuilder {

    public ClipboardDocBuilder(AnActionEvent event, DocType docType) {
        super(event, docType);
    }

    @Override
    protected void buildDoc(ApiModel model) {
        String text = buildText(model);
        if (StringUtils.isBlank(text)) {
            return;
        }
        BuilderUtil.copyToClipboard(text);
    }

    protected abstract String buildText(ApiModel model);

    @Override
    protected String getSuccessMessage() {
        return "copy " + this.docType.getName() + " success";
    }

    @Override
    protected String geErrorMessage() {
        return "copy " + this.docType.getName() + " failed";
    }

    public static ClipboardDocBuilder of(AnActionEvent event, DocType docType, Function<ApiModel, String> buildFunction) {
        return new ClipboardDocBuilder(event, docType) {
            @Override
            protected String buildText(ApiModel model) {
                return buildFunction.apply(model);
            }
        };
    }
}
