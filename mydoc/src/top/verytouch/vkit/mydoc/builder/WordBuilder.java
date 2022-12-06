package top.verytouch.vkit.mydoc.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.constant.DocType;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 导出为word
 *
 * @author verytouch
 * @since 2021-12
 */
public class WordBuilder extends FreemarkerBuilder {

    private static final String WORD_RESOURCE = "word.docx";
    private static final String TO_REPLACE_FILE = "document.xml";

    public WordBuilder(AnActionEvent event) {
        super(event, DocType.WORD);
    }

    @Override
    protected OutputStream buildDoc(String path) throws Exception {
        // 渲染document并读取为输入流
        String temp = System.getProperty("java.io.tmpdir") + TO_REPLACE_FILE;
        IOUtils.close(super.buildDoc(temp));
        File documentFile = new File(temp);
        FileInputStream documentIn = new FileInputStream(documentFile);
        // 替换document文件并输出为docx
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(path));
        ZipInputStream zipIn = new ZipInputStream(getTemplateResourceAsStream(WORD_RESOURCE));
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
