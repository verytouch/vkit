package top.verytouch.vkit.mydoc.builder.clipboard;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.builder.DocBuilder;
import top.verytouch.vkit.mydoc.builder.Result;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiModel;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

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
    protected Result buildDoc(ApiModel model) {
        String text = buildText(model);
        if (StringUtils.isBlank(text)) {
            return Result.ok("nothing to copy");
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        return Result.ok("copy " + this.docType.getName() + " success");
    }

    protected abstract String buildText(ApiModel model);

}
