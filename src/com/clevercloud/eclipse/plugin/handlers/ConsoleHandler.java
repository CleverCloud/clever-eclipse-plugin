package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import com.clevercloud.eclipse.plugin.api.CleverCloudApi;
import com.clevercloud.eclipse.plugin.ui.LoginUI;
import com.clevercloud.eclipse.plugin.ui.wizards.CleverWizard;

public class ConsoleHandler {

	private static final String API_KEY = "T8NZNyJejYkevYvKj1EWgSq0rnXABH";
	private static final String API_SECRET = "QCLYg7n9YJwlxoEI0HkMSwetuphT9Q";

	@Execute
	public void execute(Shell shell) {
		if (CleverCloudApi.accessToken == null) {
			this.executeLogin(shell);
			return;
		}
		IWizardDescriptor desc = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard("org.eclipse.jdt.ui.wizards.JavaProjectWizard");
		if (desc == null) {
			System.out.println("Langage not found, using defaut project");
			desc = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard("org.eclipse.ui.wizards.new.project");
		}
		try {
			IWizard wiz = desc.createWizard();
			WizardDialog wd = new WizardDialog(shell, wiz);
			wd.open();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void executeLogin(Shell shell) {
		CleverCloudApi.oauth = new ServiceBuilder()
		.provider(CleverCloudApi.class)
		.apiKey(API_KEY)
		.apiSecret(API_SECRET)
		.callback("https://console.clever-cloud.com/cli-oauth")
		.build();

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
