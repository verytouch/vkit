package top.verytouch.vkit.ocr.ali;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

/**
 * 阿里OCR帮助类
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Slf4j
@SuppressWarnings("unused")
public class AliOcrHelper {

    /**
     * OCR批量识别
     *
     * @param service ocr服务类
     * @param imgDir  图片文件夹，非递归
     * @param out     识别结果
     */
    public static void generalOcrBatch(AliOcrService service, String imgDir, OutputStream out) throws Exception {
        Collection<File> files = FileUtils.listFiles(new File(imgDir + "\\kd"), new String[]{"jpg", "png"}, false);
        int i = 1;
        ByteArrayOutputStream imgStream;
        try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            for (File file : files) {
                log.info("正在处理第" + i + "张");
                imgStream = new ByteArrayOutputStream();
                IOUtils.copy(new FileInputStream(file), imgStream);
                String img = Base64.getEncoder().encodeToString(imgStream.toByteArray());
                String text = getGeneralWords(service.general(img), "\n");
                writer.write("\n" + i + ".======================================\n" + text);
                i++;
            }
            writer.flush();
        }
    }

    /**
     * 从识别结果中获取word
     *
     * @param json      识别结果json
     * @param delimiter word之间的分隔符
     */
    @SuppressWarnings("unchecked")
    public static String getGeneralWords(String json, String delimiter) {
        return String.join(delimiter, JsonPath.parse(json).read("$.ret[*].word", List.class));
    }

}
