package com.clevercloud.eclipse.plugin.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

import com.clevercloud.eclipse.plugin.api.CleverCloudApi;
import com.clevercloud.eclipse.plugin.api.json.ApplicationJSON;
import com.clevercloud.eclipse.plugin.core.CloneUtils;

public class ImportWizard extends Wizard implements IImportWizard {

	private ImportSelectionPage page = null;

	public ImportWizard() {
		super();
		if (!CleverCloudApi.isAuthentified()) {
			MessageDialog.openError(getShell(), "Login Error", "You must be logged in for importing a project.");
			this.performCancel();
			return;
		}
		page = new ImportSelectionPage();
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
		Job job = new Job(selected.getName()) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				CloneUtils op = new CloneUtils(selected.getName(), selected.getDeployment().getUrl(),
						selected.getId(), selected.getParent().getId(), monitor);
				try {
					return op.execute();
				} catch (InvocationTargetException | URISyntaxException | InterruptedException | CoreException e) {
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}
			}
		};

		IWorkbenchSiteProgressService progress = PlatformUI.getWorkbench().getService(IWorkbenchSiteProgressService.class);
		if (progress != null) {
			progress.schedule(job);
		} else {
			job.schedule();
		}
		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage current) {
		return null;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
}
