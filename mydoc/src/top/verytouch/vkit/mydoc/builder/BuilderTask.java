package top.verytouch.vkit.mydoc.builder;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.model.ApiModel;
import top.verytouch.vkit.mydoc.util.BuilderUtil;
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
        ApiModel data = ApplicationManager.getApplication().<ApiModel>runReadAction(() -> {
            int scope = BuilderUtil.isInEditor(docBuilder.event) ? BuilderUtil.CURRENT_IN_EDITOR : BuilderUtil.SELECTED_IN_TREE;
            return BuilderUtil.buildModel(docBuilder.event, scope);
        });
        // 渲染模板
        progressIndicator.setFraction(0.8);
        progressIndicator.setText(String.format("Building %s...", docBuilder.docType.getName()));
        ApplicationManager.getApplication().runReadAction(() -> {
            try {
                BuilderResult builderResult = this.docBuilder.buildDoc(data);
                if (builderResult.isSuccess()) {
                    NotifyUtil.info(this.docBuilder.event.getProject(), builderResult.getMsg());
                } else {
                    NotifyUtil.error(this.docBuilder.event.getProject(), builderResult.getMsg());
                }
            } catch (Exception e) {
                String msg = StringUtils.isBlank(e.getMessage()) ? "build failed" : e.getMessage();
                NotifyUtil.error(docBuilder.event.getProject(), msg);
            } finally {
                // 完成
                progressIndicator.setFraction(1.0);
                progressIndicator.setText("Finished");
            }
        });
    }
}
