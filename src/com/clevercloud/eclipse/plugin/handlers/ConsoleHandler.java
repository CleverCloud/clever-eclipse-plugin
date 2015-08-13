package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clevercloud.eclipse.plugin.CleverNature;
import com.clevercloud.eclipse.plugin.api.CleverCloudApi;
import com.clevercloud.eclipse.plugin.core.PushUtils;
import com.clevercloud.eclipse.plugin.ui.wizards.ImportWizard;

public class ConsoleHandler extends AbstractHandler {

	private void importWizard(Shell shell) {
		WizardDialog dial = new WizardDialog(shell, new ImportWizard());
		dial.open();
	}

	private boolean executePush(Shell shell) {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor != null) {
			IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
			IFile file = input.getFile();
			IProject project = file.getProject();
			try {
				if (project.hasNature(CleverNature.NATURE_ID)) {
					PushUtils op = new PushUtils(project);
					op.execute(shell);
					return true;
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public Object execute(ExecutionEvent event) {
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		if (!CleverCloudApi.isAuthentified()) {
			CleverCloudApi.executeLogin(shell);
			return null;
		}
		if (executePush(shell) == false)
			importWizard(shell);
		return null;
	}
}
