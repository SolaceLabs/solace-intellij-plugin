package community.solace.ep.idea.plugin.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

public final class Notify {

	public static void msg(String s) {
		Notifications.Bus.notify(new Notification("ep20", "Solace Event Portal", s, NotificationType.INFORMATION));

	}
}
