package com.clevercloud.eclipse.plugin.core;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.core.op.CommitOperation;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;

public class PushUtils {

	private IProject project;
	private String commit;
	private IProgressMonitor monitor;
	private File folder;
	private RepositoryMapping mapping;

	public PushUtils(IProject project, String commit, IProgressMonitor monitor) {
		this.commit = commit;
		this.monitor = monitor;
		this.folder = new File(Platform.getLocation().toOSString() + this.project.getFullPath().toString());
		this.mapping = RepositoryMapping.getMapping(new Path(folder.getAbsolutePath()));
	}

	public IStatus execute() throws CoreException {
		if (this.mapping == null)
			return Status.CANCEL_STATUS;
		Repository repo = this.mapping.getRepository();
		for (String remote : repo.getRemoteNames()) {
			if (remote.equals("clever")) {
				commit(repo);
				push(repo);
				return Status.OK_STATUS;
			}
		}
		return Status.CANCEL_STATUS;
	}

	private void commit(Repository repo) throws CoreException {
		//TODO: Use identity of user
		PersonIdent commiter = new PersonIdent("me", "me@meme.com");
		CommitOperation commit = new CommitOperation(repo, commiter.toExternalString(), commiter.toExternalString(), this.commit);
		commit.setCommitAll(true);
		commit.execute(this.monitor);
	}

	private void push(Repository repo) {
		//TODO: Push
	}
}
