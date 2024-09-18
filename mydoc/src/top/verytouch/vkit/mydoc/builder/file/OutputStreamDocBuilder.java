package top.verytouch.vkit.mydoc.builder.file;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.builder.DocBuilder;
import top.verytouch.vkit.mydoc.config.ConfigStorage;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiModel;

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
    protected void buildDoc(ApiModel model) throws IOException {
        OutputStream outputStream = buildOutputStream(model);
        outputStream.flush();
        IOUtils.close(outputStream);
        openOutPath(model.getConfig());
    }

    protected String getOutBasePath(ConfigStorage config) {
        return StringUtils.isNotBlank(config.getOutputDir()) ? config.getOutputDir() : FileSystemView.getFileSystemView().getHomeDirectory().getPath();
    }

    protected String getOutPath(ApiModel model) {
        return getOutBasePath(model.getConfig()) + File.separator + model.getName() + docType.getSuffix();
    }

    protected abstract OutputStream buildOutputStream(ApiModel model) throws IOException;

    protected void openOutPath(ConfigStorage config) throws IOException {
        if (!config.isOpenOutDir()) {
            return;
        }
        String os = System.getProperty("os.name").toLowerCase();
        String command;
        if (os.contains("windows")) {
            command = "explorer";
        } else if (os.contains("mac")) {
            command = "open";
        } else {
            command = "xdg-open";
        }
        Runtime.getRuntime().exec(new String[]{command, getOutBasePath(config)});
    }

}