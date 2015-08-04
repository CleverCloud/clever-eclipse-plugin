package com.clevercloud.eclipse.plugin.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.Activator;
import org.eclipse.egit.core.RepositoryUtil;
import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

import com.clevercloud.eclipse.plugin.api.CleverCloudApi;
import com.clevercloud.eclipse.plugin.api.json.ApplicationJSON;

public class CleverWizard extends Wizard implements IImportWizard {

	private ImportSelectionPage page = null;

	public CleverWizard() {
		super();
		if (CleverCloudApi.accessToken == null) {
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
				RepositoryUtil repositoryUtil = Activator.getDefault().getRepositoryUtil();
				monitor.beginTask(selected.getName(), 3);
				try {
					monitor.setTaskName("Creating repository");
					IPreferenceStore store = org.eclipse.egit.ui.Activator.getDefault().getPreferenceStore();

					URIish uri = new URIish(selected.getDeployment().getUrl());
					File cloneDir = new File(Platform.getLocation().toOSString(), selected.getName());
					if (cloneDir.exists()) {
						return Status.CANCEL_STATUS;
					}
					CloneOperation op = new CloneOperation(uri, true, null, cloneDir, Constants.R_HEADS + Constants.MASTER,
							"clever", store.getInt(UIPreferences.REMOTE_CONNECTION_TIMEOUT));
					monitor.worked(1);

					monitor.setTaskName("Cloning repository");
					SubProgressMonitor sub = new SubProgressMonitor(monitor, 1);
					op.run(sub);
					sub.done();

					monitor.setTaskName("Importing repository");
					repositoryUtil.addConfiguredRepository(op.getGitDir());
					IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(selected.getName());
					File projectFile = new File(cloneDir.getAbsolutePath() + "/.project");
					if (projectFile.exists()) {
						IProjectDescription desc = ResourcesPlugin.getWorkspace().loadProjectDescription(new Path(projectFile.getAbsolutePath()));
						project.create(desc, monitor);
					} else {
						project.create(monitor);
					}
					project.open(monitor);
					monitor.worked(1);
				} catch (URISyntaxException | InvocationTargetException | InterruptedException | CoreException e) {
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}
				monitor.done();
				return Status.OK_STATUS;
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
