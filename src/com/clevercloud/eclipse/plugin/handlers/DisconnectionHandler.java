package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.browser.Browser;

import com.clevercloud.eclipse.plugin.api.CleverCloudApi;

public class DisconnectionHandler extends AbstractHandler {

	public static void disconnect() {
		CleverCloudApi.accessToken = null;
		Browser.clearSessions();
	}

	@Override
	public boolean isEnabled() {
		if (CleverCloudApi.accessToken != null) {
			return true;
		}
		return false;
	}

	@Override
	public Object execute(ExecutionEvent event) {
		DisconnectionHandler.disconnect();
		return null;
	}
}
