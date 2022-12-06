package top.verytouch.vkit.mydoc.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.constant.DocType;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.OutputStream;

/**
 * 文档生成并导出
 *
 * @author verytouch
 * @since 2021-12
 */
public abstract class OutputStreamDocBuilder extends DocBuilder {

    protected OutputStreamDocBuilder(AnActionEvent event, DocType docType) {
        super(event, docType);
    }

    /**
     * 获取默认导出位置
     */
    protected String getOutputPath() {
        return FileSystemView.getFileSystemView().getHomeDirectory().getPath() +
                File.separator + model.getName() + docType.getSuffix();
    }

    /**
     * 生成文档输出流
     */
    protected abstract OutputStream buildDoc(String path) throws Exception;

}
