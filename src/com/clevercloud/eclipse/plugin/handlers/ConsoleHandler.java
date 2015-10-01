package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clevercloud.eclipse.plugin.api.CcApi;
import com.clevercloud.eclipse.plugin.ui.wizards.ImportWizard;

public class ConsoleHandler extends AbstractHandler {

	private void importWizard(Shell shell) {
		WizardDialog dial = new WizardDialog(shell, new ImportWizard());
		dial.open();
	}

	@Override
	public Object execute(ExecutionEvent event) {
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		if (!CcApi.isAuthentified()) {
			CcApi.getInstance().executeLogin(shell);
			return null;
		}
		importWizard(shell);
		return null;
	}
}
