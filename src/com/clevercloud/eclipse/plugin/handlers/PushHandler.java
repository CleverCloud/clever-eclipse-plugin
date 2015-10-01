package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clevercloud.eclipse.plugin.CleverNature;
import com.clevercloud.eclipse.plugin.api.CcApi;
import com.clevercloud.eclipse.plugin.core.PushUtils;

public class PushHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		if (!CcApi.isAuthentified()) {
			CcApi.getInstance().executeLogin(shell);
			if (!CcApi.isAuthentified())
				return null;
		}
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveWorkbenchWindow(event)
				.getSelectionService().getSelection();
		IProject project = (IProject) selection.getFirstElement();

		try {
			if (project.hasNature(CleverNature.NATURE_ID)) {
				PushUtils op = new PushUtils(project);
				op.execute(shell);
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
}
