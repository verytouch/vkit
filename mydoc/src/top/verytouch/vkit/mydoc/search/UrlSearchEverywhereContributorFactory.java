package top.verytouch.vkit.mydoc.search;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import top.verytouch.vkit.mydoc.model.ApiOperation;

/**
 * url search everywhere
 *
 * @author verytouch
 * @since 2023-11
 */
public class UrlSearchEverywhereContributorFactory implements SearchEverywhereContributorFactory<ApiOperation> {
    @Override
    public @NotNull SearchEverywhereContributor<ApiOperation> createContributor(@NotNull AnActionEvent anActionEvent) {
        return new UrlSearchEverywhereContributor(anActionEvent);
    }
}
