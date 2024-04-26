package top.verytouch.vkit.mydoc.util;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;


/**
 * 日志输出到idea右下角event log
 *
 * @author verytouch
 * @since 2021-11
 */
public class NotifyUtil {

    public static void log(@Nullable Project project, NotificationType notificationType, String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("MyDoc.Notification.Group")
                .createNotification(content, notificationType)
                .notify(project);
    }

    public static void error(@Nullable Project project, String content) {
        log(project, NotificationType.ERROR, content);
    }

    public static void info(@Nullable Project project, String content) {
        log(project, NotificationType.INFORMATION, content);
    }

    public static void warn(@Nullable Project project, String content) {
        log(project, NotificationType.WARNING, content);
    }
}
