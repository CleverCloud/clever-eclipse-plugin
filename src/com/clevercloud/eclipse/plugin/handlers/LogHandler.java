package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class LogHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) {
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveWorkbenchWindow(event)
				.getSelectionService().getSelection();
		IProject project = (IProject) selection.getFirstElement();
		System.out.println(project.getName());
		//String logs = CleverCloudApi.logRequest("app_2c98f932-745c-4a00-87c0-01735a771f06", 300);
		//System.out.println(logs);
		return null;
	}
}
