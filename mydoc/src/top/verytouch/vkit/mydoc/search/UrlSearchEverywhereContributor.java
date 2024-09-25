package top.verytouch.vkit.mydoc.search;

import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.searcheverywhere.*;
import com.intellij.idea.ActionsBundle;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.util.Processor;
import com.intellij.util.PsiNavigateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.verytouch.vkit.mydoc.model.ApiOperation;
import top.verytouch.vkit.mydoc.util.BuilderUtil;

import javax.swing.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 查找逻辑
 *
 * @author verytouch
 * @since 2023-11
 */
public class UrlSearchEverywhereContributor implements WeightedSearchEverywhereContributor<ApiOperation> {

    private final AnActionEvent event;
    private final Project project;
    private List<ApiOperation> operationList;
    private final PersistentSearchEverywhereContributorFilter<String> moduleFilter;
    private final List<String> modules = Collections.synchronizedList(new LinkedList<>());

    public UrlSearchEverywhereContributor(@NotNull AnActionEvent event) {
        this.event = event;
        this.project = event.getProject();
        assert project != null;
        this.moduleFilter = new PersistentSearchEverywhereContributorFilter<>(
                modules,
                UrlSearchModuleFilterConfiguration.getInstance(project),
                Function.identity(),
                s -> AllIcons.Nodes.Module
        );
    }

    @Override
    public void fetchWeightedElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super FoundItemDescriptor<ApiOperation>> processor) {
        if (isDumbAware()) {
            return;
        }
        // ALL Tab
        if (ActionsBundle.message("action.SearchEverywhere.text").equals(this.event.getPresentation().getText()) && StringUtils.isBlank(pattern)) {
            return;
        }
        SearchEverywhereManager seManager = SearchEverywhereManager.getInstance(this.project);
        // 其他Tab
        if (seManager.isShown() && !getSearchProviderId().equals(seManager.getSelectedTabID()) && StringUtils.isBlank(pattern)) {
            return;
        }
        if (operationList == null) {
            this.modules.clear();
            ModuleUtil.getModulesOfType(this.project, JavaModuleType.getModuleType()).forEach(m -> this.modules.add(m.getName()));
            Computable<List<ApiOperation>> apiSupplier = () -> BuilderUtil.buildModel(this.event, BuilderUtil.ALL_IN_PROJECT)
                    .getData().stream()
                    .flatMap(g -> g.getOperationList().stream().peek(a -> a.setPath(g.getPath() + a.getPath())))
                    .collect(Collectors.toList());
            this.operationList = ApplicationManager.getApplication().runReadAction(apiSupplier);
        }
        if (CollectionUtils.isEmpty(this.moduleFilter.getAllElements()) || CollectionUtils.isEmpty(this.moduleFilter.getSelectedElements())) {
            return;
        }
        Set<String> selectedModules = new HashSet<>(this.moduleFilter.getSelectedElements());
        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + pattern + "*", NameUtil.MatchingCaseSensitivity.NONE);
        for (ApiOperation item : this.operationList) {
            Module apiModule = ModuleUtil.findModuleForFile(item.getPsiMethod().getContainingFile());
            if (apiModule != null && selectedModules.contains(apiModule.getName()) && matcher.matches(item.getPath())) {
                if (!processor.process(new FoundItemDescriptor<>(item, matcher.matchingDegree(item.getPath())))) {
                    return;
                }
            }
        }
    }

    @Override
    public @NotNull List<AnAction> getActions(@NotNull Runnable onChanged) {
        return Collections.singletonList(new SearchEverywhereFiltersAction<>(this.moduleFilter, onChanged));
    }

    @Nullable
    @Override
    public String getAdvertisement() {
        return isDumbAware() ? "Results might be incomplete. The project is being indexed." : "type url or to search";
    }

    @Override
    public @NotNull String getSearchProviderId() {
        return UrlSearchEverywhereContributor.class.getName();
    }

    @Override
    public @NotNull
    @Nls String getGroupName() {
        return "Url";
    }

    @Override
    public int getSortWeight() {
        return 800;
    }

    @Override
    public boolean showInFindResults() {
        return false;
    }

    @Override
    public boolean processSelectedItem(@NotNull ApiOperation apiOperation, int i, @NotNull String s) {
        PsiNavigateUtil.navigate(apiOperation.getPsiMethod());
        return true;
    }

    @Override
    public boolean isShownInSeparateTab() {
        return true;
    }

    @Override
    public boolean isEmptyPatternSupported() {
        return true;
    }

    @Override
    public boolean isDumbAware() {
        return DumbService.isDumb(this.project);
    }

    @Override
    public Object getDataForItem(@NotNull ApiOperation apiOperation, @NotNull String s) {
        return null;
    }

    @Override
    public @NotNull ListCellRenderer<? super ApiOperation> getElementsRenderer() {
        return new UrlSearchEverywherePsiRenderer(this);
    }
}
