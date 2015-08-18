package com.clevercloud.eclipse.plugin.handlers;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clevercloud.eclipse.plugin.api.CcApi;
import com.clevercloud.eclipse.plugin.core.PreferencesUtils;
import com.clevercloud.eclipse.plugin.core.WebSocketCore;

public class LogHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveWorkbenchWindow(event)
				.getSelectionService().getSelection();
		IProject project = (IProject) selection.getFirstElement();
		PreferencesUtils prefs = new PreferencesUtils(project, false);
		CcApi.getInstance().logRequest(prefs.getId(), 300);

		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:s.S'Z'");
		df.setTimeZone(tz);
		String timestamp = df.format(new Date());

		try {
			URI uri = new URI("wss://logs-api.clever-cloud.com/logs-socket/" + prefs.getId() + "?since=" + timestamp);
			WebSocketCore ws = new WebSocketCore(uri);
			ws.connectBlocking();
		} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
