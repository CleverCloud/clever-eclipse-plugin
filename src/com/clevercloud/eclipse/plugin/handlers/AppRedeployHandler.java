package com.clevercloud.eclipse.plugin.handlers;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clevercloud.eclipse.plugin.api.CcApi;
import com.clevercloud.eclipse.plugin.api.json.logs.LogsSocketSourceJSON;
import com.clevercloud.eclipse.plugin.core.PreferencesUtils;
import com.clevercloud.eclipse.plugin.ui.NotificationUI;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppRedeployHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveWorkbenchWindow(event)
				.getSelectionService().getSelection();
		IProject project = (IProject) selection.getFirstElement();
		PreferencesUtils prefs = new PreferencesUtils(project, false);

		String resp = CcApi.getInstance().apiPost(CcApi.getOrgaUrl(prefs.getOrga()) + "/applications/"
		+ prefs.getId() + "/instances", null);
		ObjectMapper mapper = new ObjectMapper();
		try {
			LogsSocketSourceJSON log = mapper.readValue(resp, LogsSocketSourceJSON.class);
			NotificationUI.sendNotif(log.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
