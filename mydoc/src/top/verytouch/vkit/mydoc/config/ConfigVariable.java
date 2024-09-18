package top.verytouch.vkit.mydoc.config;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 * 用于在配置面版配置动态参数
 *
 * @author verytouch
 * @since 2021-12
 */
@Getter
@RequiredArgsConstructor
public enum ConfigVariable {

    PROJECT_NAME("${projectName}", "项目名", event -> {
        Project project = event.getProject();
        return project == null ? "" : project.getName();
    }),

    MODULE_NAME("${moduleName}", "模块名", event -> {
        Module module = event.getData(LangDataKeys.MODULE);
        return module == null ? "" : module.getName();
    });

    /**
     * 变量
     */
    private final String variable;

    /**
     * 说明
     */
    private final String remark;

    /**
     * 变量替换为真实值的方法
     */
    private final Function<AnActionEvent, String> mapper;
}
