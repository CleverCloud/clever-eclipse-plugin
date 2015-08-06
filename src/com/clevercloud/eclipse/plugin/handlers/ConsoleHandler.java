package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import com.clevercloud.eclipse.plugin.api.CleverCloudApi;
import com.clevercloud.eclipse.plugin.core.PushUtils;
import com.clevercloud.eclipse.plugin.ui.LoginUI;
import com.clevercloud.eclipse.plugin.ui.wizards.CleverWizard;

public class ConsoleHandler {

	@Execute
	public void execute(Shell shell) {
		if (CleverCloudApi.accessToken == null) {
			this.executeLogin(shell);
			return;
		}
		if (executePush(shell) == false)
			importWizard(shell);
	}

	private void executeLogin(Shell shell) {
		Token requestToken = CleverCloudApi.oauth.getRequestToken();
		String authURL = CleverCloudApi.oauth.getAuthorizationUrl(requestToken);
		LoginUI login = new LoginUI(shell, authURL);
		login.openLogin();

		if (CleverCloudApi.oauthVerifier == null)
			return;
		Verifier verifier = new Verifier(CleverCloudApi.oauthVerifier);
		CleverCloudApi.accessToken = CleverCloudApi.oauth.getAccessToken(requestToken, verifier);
	}

	private void importWizard(Shell shell) {
		WizardDialog dial = new WizardDialog(shell, new CleverWizard());
		dial.open();
	}

	private boolean executePush(Shell shell) {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor != null) {
			IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
			IFile file = input.getFile();
			IProject project = file.getProject();
			PushUtils op = new PushUtils(project);
			if (op.execute(shell))
				return true;
		}
		return false;
	}
}
