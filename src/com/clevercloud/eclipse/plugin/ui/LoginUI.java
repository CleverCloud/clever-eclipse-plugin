package com.clevercloud.eclipse.plugin.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class LoginUI {

	private static final String LOGIN_URL = "https://api.clever-cloud.com/v2/session/login";
	private static final String OAUTH_URL = "https://api.clever-cloud.com/v2/oauth/";
	private static final String CONSOLE_URL = "https://console.clever-cloud.com/";
	private static final String GITHUB_LOGIN_URL = "https://github.com/login";

	private Shell shell;
	private Display display;
	private FillLayout layout = new FillLayout();
	private Browser browser;

	public LoginUI(Shell shell, String url) {
		this.display = shell.getDisplay();
		this.shell = new Shell(shell, SWT.SHEET);
		this.shell.setSize(550, 850);
		this.shell.setLayout(layout);
		this.browser = new Browser(this.shell, SWT.NONE);
		this.browser.setUrl(url);
	}

	public String openLogin() {
		String callbackUrl = null;
		this.shell.open();

		while (!this.shell.isDisposed()) {
			if (!(browser.getUrl().startsWith(LOGIN_URL)
					|| browser.getUrl().equals("about:blank")
					|| browser.getUrl().startsWith(GITHUB_LOGIN_URL) || browser
					.getUrl().startsWith(OAUTH_URL))) {
				if (browser.getUrl().startsWith(CONSOLE_URL)) {
					callbackUrl = browser.getUrl();
				} else {
					try {
						Desktop.getDesktop().browse(new URI(browser.getUrl()));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
				}
				this.browser.dispose();
				this.shell.close();
				return callbackUrl;
			}
			if (!display.readAndDispatch())
				display.sleep();
		}
		return callbackUrl;
	}
}
