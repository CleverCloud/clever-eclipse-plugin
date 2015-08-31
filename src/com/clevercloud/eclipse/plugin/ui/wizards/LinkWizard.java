package com.clevercloud.eclipse.plugin.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.clevercloud.eclipse.plugin.CleverNature;
import com.clevercloud.eclipse.plugin.api.CcApi;
import com.clevercloud.eclipse.plugin.api.json.ApplicationJSON;
import com.clevercloud.eclipse.plugin.core.PreferencesUtils;
import com.clevercloud.eclipse.plugin.ui.NotificationUI;

public class LinkWizard extends Wizard {

	private AppTreeSelectionPage page = null;
	private IProject project;

	public LinkWizard(IProject project) {
		super();
		if (!CcApi.isAuthentified()) {
			MessageDialog.openError(getShell(), "Login Error", "You must be logged in for importing a project.");
			this.performCancel();
			return;
		}
		page = new AppTreeSelectionPage();
		this.project = project;
	}

	@Override
	public String getWindowTitle() {
		return "CleverCloud Project Wizard";
	}

	@Override
	public void addPages() {
		if (page != null)
			addPage(page);
	}

	@Override
	public boolean performFinish() {
		final ApplicationJSON selected = this.page.getSelectedItem();
		PreferencesUtils prefs = new PreferencesUtils(project, false);
		prefs.setId(selected.getId());
		prefs.setName(selected.getName());
		prefs.setOrga(selected.getParent().getId());
		prefs.setGitUrl(selected.getDeployment().getUrl());
		prefs.save();
		try {
			CleverNature.addProjectNature(project, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		NotificationUI.sendNotif(selected.getName() +  " linked to " + project.getName() + " successfully.");
		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage current) {
		return null;
	}
}
