package top.verytouch.vkit.mydoc.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiModel;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * 文档生成
 *
 * @author verytouch
 * @since 2021-12
 */
public final class VoidDocBuilder extends DocBuilder {

    private final Consumer<ApiModel> consumer;

    public VoidDocBuilder(AnActionEvent event, DocType docType, Consumer<ApiModel> consumer) {
        super(event, docType);
        this.consumer = consumer;
    }

    @Override
    protected void buildDoc() throws IOException {
        consumer.accept(model);
    }
}
