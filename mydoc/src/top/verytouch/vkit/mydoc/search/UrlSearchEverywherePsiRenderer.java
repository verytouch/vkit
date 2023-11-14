package top.verytouch.vkit.mydoc.search;

import com.intellij.ide.actions.SearchEverywherePsiRenderer;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.lang3.StringUtils;
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
        try {
            Color fgColor = list.getForeground();
            Color bgColor = UIUtil.getListBackground();
            SimpleTextAttributes nameAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, fgColor);

            ItemMatchers itemMatchers = getItemMatchers(list, value);
            ApiOperation api = (ApiOperation) value;
            String name = api.getPath();
            String locationString = "    " + ((PsiClass) api.getPsiMethod().getParent()).getName() + "." + api.getPsiMethod().getName();

            SpeedSearchUtil.appendColoredFragmentForMatcher(name, renderer, nameAttributes, itemMatchers.nameMatcher, bgColor, selected);
            // renderer.setIcon(IconHolder.getHttpMethodIcon(api.getHttpMethod()));

            if (StringUtils.isNotEmpty(locationString)) {
                FontMetrics fm = list.getFontMetrics(list.getFont());
                int maxWidth = list.getWidth() - fm.stringWidth(name) - myRightComponentWidth - 36;
                int fullWidth = fm.stringWidth(locationString);
                if (fullWidth < maxWidth) {
                    SpeedSearchUtil.appendColoredFragmentForMatcher(locationString, renderer, SimpleTextAttributes.GRAYED_ATTRIBUTES, itemMatchers.nameMatcher, bgColor, selected);
                } else {
                    int adjustedWidth = Math.max(locationString.length() * maxWidth / fullWidth - 1, 3);
                    locationString = StringUtil.trimMiddle(locationString, adjustedWidth);
                    SpeedSearchUtil.appendColoredFragmentForMatcher(locationString, renderer, SimpleTextAttributes.GRAYED_ATTRIBUTES, itemMatchers.nameMatcher, bgColor, selected);
                }
            }
            return true;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
