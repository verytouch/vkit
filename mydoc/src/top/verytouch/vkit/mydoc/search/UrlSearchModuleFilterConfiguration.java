package top.verytouch.vkit.mydoc.search;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;

/**
 * module filter选项持久化
 *
 * @author verytouch
 * @since 2021-12
 */
@State(name = "UrlSearchModuleFilterConfiguration", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class UrlSearchModuleFilterConfiguration extends ChooseByNameFilterConfiguration<String> {

    public static UrlSearchModuleFilterConfiguration getInstance(Project project) {
        return project.getService(UrlSearchModuleFilterConfiguration.class);
    }

    @Override
    protected String nameForElement(String s) {
        return s;
    }
}
