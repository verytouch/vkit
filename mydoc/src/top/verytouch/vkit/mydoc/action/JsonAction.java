package top.verytouch.vkit.mydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.builder.OutputStreamDocBuilder;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.util.JsonUtil;

import java.io.FileOutputStream;
import java.io.IOException;
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
            protected OutputStream buildOutputStream() throws IOException {
                FileOutputStream outputStream = new FileOutputStream(getOutPath());
                JsonUtil.toJson(model, outputStream);
                return outputStream;
            }
        }.build();
    }

}
