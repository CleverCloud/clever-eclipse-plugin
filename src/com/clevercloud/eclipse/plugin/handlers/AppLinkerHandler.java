package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clevercloud.eclipse.plugin.api.CcApi;
import com.clevercloud.eclipse.plugin.ui.wizards.LinkWizard;

public class AppLinkerHandler extends AbstractHandler {

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

		WizardDialog dial = new WizardDialog(shell, new LinkWizard(project));
		dial.open();
		return null;
	}
}
