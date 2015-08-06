package com.clevercloud.eclipse.plugin.core;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.op.CommitOperation;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

public class PushUtils {

	private IProject project;
	private File folder;
	private RepositoryMapping mapping;

	public PushUtils(IProject project) {
		this.project = project;
		this.folder = new File(Platform.getLocation().toOSString() + this.project.getFullPath().toString());
		this.mapping = RepositoryMapping.getMapping(new Path(folder.getAbsolutePath()));
	}

	public boolean execute(Shell shell) {
		IWorkbenchSiteProgressService progress = PlatformUI.getWorkbench().getService(IWorkbenchSiteProgressService.class);
		if (this.mapping == null)
			return false;
		final Repository repo = this.mapping.getRepository();

		for (String remote : repo.getRemoteNames()) {
			//TODO: Regex match repo
			if (remote.equals("clever")) {
				final String commitMessage = getCommitMessage(shell);
				Job job = new Job(this.project.getName()) {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							commit(repo, commitMessage, monitor);
							push(repo, monitor);
						} catch (CoreException e) {
							e.printStackTrace();
							return Status.CANCEL_STATUS;
						}
						return Status.OK_STATUS;
					}
				};
				if (progress != null) {
					progress.schedule(job);
				} else {
					job.schedule();
				}
				return true;
			}
		}
		return false;
	}

	private void commit(Repository repo, String commitMessage, IProgressMonitor monitor) throws CoreException {
		//TODO: Use identity of user
		PersonIdent commiter = new PersonIdent("Wilson", "lost@perdu.com");
		CommitOperation commit = new CommitOperation(repo, commiter.toExternalString(), commiter.toExternalString(), commitMessage);
		commit.setCommitAll(true);
		commit.execute(monitor);
	}

	private String getCommitMessage(Shell shell) {
		String commit = "Commited by Clever Cloud Eclipse plugin.";
		//TODO: Commit Message Dialog
		return commit;
	}

	private void push(Repository repo, IProgressMonitor monitor) {
		//TODO: Push
	}
}
