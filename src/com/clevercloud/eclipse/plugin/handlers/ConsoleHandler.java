package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import com.clevercloud.eclipse.plugin.api.CleverCloudApi;
import com.clevercloud.eclipse.plugin.ui.LoginUI;
import com.clevercloud.eclipse.plugin.ui.wizards.CleverWizard;

public class ConsoleHandler {

	@Execute
	public void execute(Shell shell) {
		if (CleverCloudApi.accessToken == null) {
			this.executeLogin(shell);
			return;
		}
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
		//System.out.println(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().);
		WizardDialog dial = new WizardDialog(shell, new CleverWizard());
		dial.open();
	}
}
