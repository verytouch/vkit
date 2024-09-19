package top.verytouch.vkit.mydoc.search;

import com.intellij.ide.actions.SearchEverywhereBaseAction;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * url search everywhere
 *
 * @author verytouch
 * @since 2023-11
 */
public class UrlSearchEverywhereAction extends SearchEverywhereBaseAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        showInSearchEverywherePopup(UrlSearchEverywhereContributor.class.getSimpleName(), e, true, true);
    }
}
