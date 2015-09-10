package com.clevercloud.eclipse.plugin.ui;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.internal.commons.notifications.ui.NotificationsPlugin;

import com.clevercloud.eclipse.plugin.api.CcApi;
import com.clevercloud.eclipse.plugin.api.json.EventJSON;
import com.clevercloud.eclipse.plugin.core.WebSocketCore;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("restriction")
public class NotificationUI extends AbstractNotification {

	private static final String NOTIFICATION_ID = "com.clevercloud.eclipse.plugin.notification.event";

	private static WebSocketCore eventSocket = null;

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

	public static void start() {
		if (CcApi.isAuthentified()) {
			try {
				URI uri = new URI("wss://api.clever-cloud.com:443/v2/events/event-socket");
				eventSocket = new WebSocketCore(uri, null);
				eventSocket.connect();
			} catch (URISyntaxException | KeyManagementException | NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}

	public static void stop() {
		if (eventSocket != null) {
			eventSocket.close();
			eventSocket = null;
		}
	}

	public static void sendNotif(String message) {
		NotificationUI notif = new NotificationUI(message);
		NotificationsPlugin.getDefault().getService().notify(Collections.singletonList(notif));
	}

	public static void translateNotif(String rawMessage) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			EventJSON event = mapper.readValue(rawMessage, EventJSON.class);
			String message = event.getMessage();
			if (message != null)
				sendNotif(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
