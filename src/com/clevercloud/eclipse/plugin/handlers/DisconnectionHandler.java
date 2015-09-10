package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;

import com.clevercloud.eclipse.plugin.api.CcApi;
import com.clevercloud.eclipse.plugin.ui.NotificationUI;

public class DisconnectionHandler extends AbstractHandler {

	@Override
	public boolean isEnabled() {
		if (CcApi.isAuthentified()) {
			return true;
		}
		return false;
	}

	@Override
	public Object execute(ExecutionEvent event) {
		CcApi.disconnect();
		NotificationUI.stop();
		return null;
	}
}
