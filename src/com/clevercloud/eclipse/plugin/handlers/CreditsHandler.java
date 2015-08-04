package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class CreditsHandler {

	@Execute
	public void execute(Shell shell) {
		MessageDialog.openInformation(shell, "Credits",
				"\nIcons are under Creative Commons Attribution 2.5 License\n"
				+ "http://www.famfamfam.com/lab/icons/silk/");
	}

}
