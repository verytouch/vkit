package top.verytouch.vkit.mydoc.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.io.IOUtils;
import top.verytouch.vkit.mydoc.constant.DocType;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
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

    @Override
    protected void buildDoc() throws IOException {
        OutputStream outputStream = buildOutputStream();
        outputStream.flush();
        IOUtils.close(outputStream);
    }

    protected String getOutPath() {
        return FileSystemView.getFileSystemView().getHomeDirectory().getPath() + File.separator + model.getName() + docType.getSuffix();
    }

    protected abstract OutputStream buildOutputStream() throws IOException;

}
