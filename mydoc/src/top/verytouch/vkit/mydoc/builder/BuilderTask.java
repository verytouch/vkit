package top.verytouch.vkit.mydoc.builder;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.util.NotifyUtil;

/**
 * 文档生成Task
 *
 * @author verytouch
 * @since 2021-12
 */
public class BuilderTask extends Task.Backgroundable {

    private final DocBuilder docBuilder;

    public BuilderTask(DocBuilder docBuilder) {
        super(docBuilder.event.getProject(), "MyDoc", false);
        this.docBuilder = docBuilder;
    }

    /**
     * 执行构建任务
     * idea下方出现进度条
     * buildApi -> buildDoc
     */
    public static void start(DocBuilder docBuilder) {
        ProgressManager.getInstance().run(new BuilderTask(docBuilder));
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        // 生成接口数据
        progressIndicator.setFraction(0.1);
        progressIndicator.setText("Building API...");
        ApplicationManager.getApplication().runReadAction(docBuilder::buildApi);
        // 渲染模板
        progressIndicator.setFraction(0.8);
        progressIndicator.setText(String.format("Building %s...", docBuilder.docType.getName()));

        try {
            this.docBuilder.buildDoc();
            if (StringUtils.isNotBlank(this.docBuilder.getSuccessMessage())) {
                NotifyUtil.info(this.docBuilder.event.getProject(), this.docBuilder.getSuccessMessage());
            }
        } catch (Exception e) {
            NotifyUtil.error(docBuilder.event.getProject(), this.docBuilder.geErrorMessage() + ": " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            // 完成
            progressIndicator.setFraction(1.0);
            progressIndicator.setText("Finished");
        }
    }
}
