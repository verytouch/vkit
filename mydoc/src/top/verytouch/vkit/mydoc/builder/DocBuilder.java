package top.verytouch.vkit.mydoc.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiModel;

import java.io.IOException;

/**
 * 文档生成
 *
 * @author verytouch
 * @since 2021-12
 */
public abstract class DocBuilder {

    protected final AnActionEvent event;
    protected final DocType docType;

    protected DocBuilder(AnActionEvent event, DocType docType) {
        this.event = event;
        this.docType = docType;
    }

    /**
     * 生成文档
     */
    protected abstract void buildDoc(ApiModel model) throws IOException;

    protected String getSuccessMessage() {
        return "build " + this.docType.getName() + " success";
    }

    protected String geErrorMessage() {
        return "build " + this.docType.getName() + " failed";
    }


}
