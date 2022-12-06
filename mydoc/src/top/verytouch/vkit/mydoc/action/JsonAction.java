package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import top.verytouch.vkit.mydoc.builder.OutputStreamDocBuilder;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.util.JsonUtil;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 导出为Json
 *
 * @author verytouch
 * @since 2021-11
 */
public class JsonAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new OutputStreamDocBuilder(event, DocType.JSON) {
            @Override
            protected OutputStream buildDoc(String path) throws Exception {
                FileOutputStream outputStream = new FileOutputStream(path);
                JsonUtil.toJson(model, outputStream);
                return outputStream;
            }
        }.build();
    }

}
