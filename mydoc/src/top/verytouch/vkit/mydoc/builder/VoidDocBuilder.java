package top.verytouch.vkit.mydoc.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.constant.DocType;

/**
 * 文档生成
 *
 * @author verytouch
 * @since 2021-12
 */
public abstract class VoidDocBuilder extends DocBuilder {

    protected VoidDocBuilder(AnActionEvent event, DocType docType) {
        super(event, docType);
    }

    protected abstract void buildDoc();
}
