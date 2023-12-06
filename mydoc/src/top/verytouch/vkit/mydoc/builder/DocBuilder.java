package top.verytouch.vkit.mydoc.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiModel;
import top.verytouch.vkit.mydoc.util.BuilderUtil;

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
    protected ApiModel model;

    protected DocBuilder(AnActionEvent event, DocType docType) {
        this.event = event;
        this.docType = docType;
    }

    /**
     * 生成接口
     */
    protected void buildApi() {
        int scope = BuilderUtil.isInEditor(event) ? BuilderUtil.CURRENT_IN_EDITOR : BuilderUtil.SELECTED_IN_TREE;
        this.model = BuilderUtil.buildModel(event, scope);
    }

    /**
     * 生成文档
     */
    protected abstract void buildDoc() throws IOException;

    protected String getSuccessMessage() {
        return "build " + this.docType.getName() + " success";
    }

    protected String geErrorMessage() {
        return "build " + this.docType.getName() + " failed";
    }


}
