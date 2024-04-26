package top.verytouch.vkit.mydoc.search;

import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.searcheverywhere.*;
import com.intellij.idea.ActionsBundle;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.util.Processor;
import com.intellij.util.PsiNavigateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.verytouch.vkit.mydoc.model.ApiGroup;
import top.verytouch.vkit.mydoc.model.ApiOperation;
import top.verytouch.vkit.mydoc.util.BuilderUtil;

import javax.swing.*;
import java.util.*;
import java.util.function.Consumer;
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
        SearchEverywhereManager seManager = SearchEverywhereManager.getInstance(project);
        // 非 All Tab
        if (seManager.isShown() && !getSearchProviderId().equals(seManager.getSelectedTabID()) && StringUtils.isBlank(pattern)) {
            return;
        }
        // ALL Tab
        if (ActionsBundle.message("action.SearchEverywhere.text").equals(event.getPresentation().getText()) && StringUtils.isBlank(pattern)) {
            return;
        }
        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + pattern + "*", NameUtil.MatchingCaseSensitivity.NONE);
        if (operationList == null) {
            fetchData();
            fetchWeightedElements(pattern, progressIndicator, processor);
        } else {
            if (CollectionUtils.isEmpty(moduleFilter.getAllElements()) || CollectionUtils.isEmpty(moduleFilter.getSelectedElements())) {
                return;
            }
            Set<String> modules = new HashSet<>(moduleFilter.getSelectedElements());
            for (ApiOperation item : operationList) {
                Module module = ModuleUtil.findModuleForFile(item.getPsiMethod().getContainingFile());
                if (module != null && modules.contains(module.getName()) && matcher.matches(item.getPath())) {
                    if (!processor.process(new FoundItemDescriptor<>(item, matcher.matchingDegree(item.getPath())))) {
                        return;
                    }
                }
            }
        }
    }

    private void fetchData() {
        try {
            Set<String> moduleNames = new HashSet<>();
            Consumer<ApiGroup> moduleNameCollector = g -> {
                if (!g.getOperationList().isEmpty()) {
                    Module module = ModuleUtil.findModuleForFile(g.getOperationList().get(0).getPsiMethod().getContainingFile());
                    if (module != null) {
                        moduleNames.add(module.getName());
                    }
                }
            };
            this.operationList = ApplicationManager.getApplication().runReadAction((ThrowableComputable<List<ApiOperation>, Throwable>) () ->
                    BuilderUtil.buildModel(this.event, BuilderUtil.ALL_IN_PROJECT).getData()
                            .stream().peek(moduleNameCollector)
                            .flatMap(g -> g.getOperationList().stream().peek(a -> a.setPath(g.getPath() + a.getPath())))
                            .collect(Collectors.toList()));
            this.modules.clear();
            this.modules.addAll(moduleNames);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull List<AnAction> getActions(@NotNull Runnable onChanged) {
        return Collections.singletonList(new SearchEverywhereFiltersAction<>(moduleFilter, onChanged));
    }

    @Nullable
    @Override
    public String getAdvertisement() {
        return DumbService.isDumb(project) ? "Results might be incomplete. The project is being indexed." : "type url or to search";
    }

    @Override
    public @NotNull String getSearchProviderId() {
        return getClass().getSimpleName();
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
        return DumbService.isDumb(project);
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
