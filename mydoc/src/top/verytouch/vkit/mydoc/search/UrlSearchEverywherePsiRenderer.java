package top.verytouch.vkit.mydoc.search;

import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.SearchEverywherePsiRenderer;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiClass;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.TextWithIcon;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;
import top.verytouch.vkit.mydoc.model.ApiOperation;

import javax.swing.*;
import java.awt.*;

/**
 * 列表渲染
 *
 * @author verytouch
 * @since 2023-11
 */
public class UrlSearchEverywherePsiRenderer extends SearchEverywherePsiRenderer {

    public UrlSearchEverywherePsiRenderer(Disposable disposable) {
        super(disposable);
    }

    @Override
    protected boolean customizeNonPsiElementLeftRenderer(ColoredListCellRenderer renderer, JList list, Object value, int index, boolean selected, boolean hasFocus) {
        ApplicationManager.getApplication().runReadAction(() -> {
            Color fgColor = list.getForeground();
            Color bgColor = UIUtil.getListBackground();
            ApiOperation api = (ApiOperation) value;
            String path = api.getPath();
            String desc = String.format(" (%s) [%s] ", ((PsiClass) api.getPsiMethod().getParent()).getName(), api.getMethod());

            SpeedSearchUtil.appendColoredFragmentForMatcher(path, renderer, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, fgColor), getItemMatchers(list, value).nameMatcher, bgColor, selected);
            SpeedSearchUtil.appendColoredFragmentForMatcher(desc, renderer, SimpleTextAttributes.GRAYED_ATTRIBUTES, null, bgColor, selected);
            renderer.setIcon(AllIcons.Nodes.Servlet);
        });
        return true;
    }

    @Override
    protected TextWithIcon getItemLocation(Object value) {
        if (!(value instanceof ApiOperation)) {
            return super.getItemLocation(value);
        }
        ApiOperation api = (ApiOperation) value;
        Module module = ModuleUtil.findModuleForFile(api.getPsiMethod().getContainingFile());
        String moduleName = module == null ? "" : module.getName();
        return new TextWithIcon(moduleName, AllIcons.Nodes.Module);
    }
}
