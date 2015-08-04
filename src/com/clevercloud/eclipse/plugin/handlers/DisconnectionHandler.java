package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;

import com.clevercloud.eclipse.plugin.api.CleverCloudApi;

public class DisconnectionHandler {

	public static void disconnect() {
		CleverCloudApi.accessToken = null;
		Browser.clearSessions();
	}
	
	@CanExecute
	public boolean canExecute() {
		if (CleverCloudApi.accessToken != null) {
			return true;
		}
		return false;
	}
	
	@Execute
	public void execute(Shell shell) {
		DisconnectionHandler.disconnect();
	}
}
