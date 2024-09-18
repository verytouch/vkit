package top.verytouch.vkit.mydoc.builder.file;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.commons.io.IOUtils;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 导出为word
 *
 * @author verytouch
 * @since 2021-12
 */
public class WordDocDocBuilder extends FreemarkerDocBuilder {

    private static final String WORD_RESOURCE = "word.docx";
    private static final String TO_REPLACE_FILE = "document.xml";

    public WordDocDocBuilder(AnActionEvent event) {
        super(event, DocType.WORD);
    }

    @Override
    protected String getOutPath(ApiModel model) {
        return System.getProperty("java.io.tmpdir") + TO_REPLACE_FILE;
    }

    @Override
    protected OutputStream buildOutputStream(ApiModel model) throws IOException {
        // 渲染document并读取为输入流
        IOUtils.close(super.buildOutputStream(model));
        File documentFile = new File(this.getOutPath(model));
        FileInputStream documentIn = new FileInputStream(documentFile);
        // 替换document文件并输出为docx
        ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(Paths.get(super.getOutPath(model))));
        ZipInputStream zipIn = new ZipInputStream(getTemplateResourceAsStream(WORD_RESOURCE, model.getConfig()));
        for (ZipEntry entry = zipIn.getNextEntry(); entry != null; entry = zipIn.getNextEntry()) {
            if (entry.getName().endsWith(TO_REPLACE_FILE)) {
                zipOut.putNextEntry(new ZipEntry(entry.getName()));
                IOUtils.copy(documentIn, zipOut);
                IOUtils.close(documentIn);
                documentFile.delete();
            } else {
                ZipEntry outEntry = new ZipEntry(entry);
                outEntry.setCompressedSize(-1);
                zipOut.putNextEntry(outEntry);
                IOUtils.copy(zipIn, zipOut);
            }
            zipOut.closeEntry();
        }
        IOUtils.close(zipIn);
        return zipOut;
    }

}
