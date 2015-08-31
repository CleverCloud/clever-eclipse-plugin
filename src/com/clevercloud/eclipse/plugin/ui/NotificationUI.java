package com.clevercloud.eclipse.plugin.ui;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.internal.commons.notifications.ui.NotificationsPlugin;

@SuppressWarnings("restriction")
public class NotificationUI extends AbstractNotification {

	private static final String NOTIFICATION_ID = "com.clevercloud.eclipse.plugin.notification.event";

	private String message;

	public NotificationUI(String message) {
		super(NOTIFICATION_ID);
		this.message = message;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public Date getDate() {
		return Calendar.getInstance().getTime();
	}

	@Override
	public String getDescription() {
		return message;
	}

	@Override
	public String getLabel() {
		return "Clever-Cloud";
	}

	public static void sendNotif(String message) {
		NotificationUI notif = new NotificationUI(message);
		NotificationsPlugin.getDefault().getService().notify(Collections.singletonList(notif));
	}
}
