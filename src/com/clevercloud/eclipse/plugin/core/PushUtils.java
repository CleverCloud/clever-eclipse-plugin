package com.clevercloud.eclipse.plugin.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.op.PushOperation;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

@SuppressWarnings("restriction")
public class PushUtils {

	private IProject project;
	private PreferencesUtils prefs;
	private File folder;
	private RepositoryMapping mapping;

	private static final String REMOTE_NAME = "com.clevercloud.eclipse.plugin.remote.temp";

	public PushUtils(IProject project) {
		this.project = project;
		this.prefs = new PreferencesUtils(project, true);
		this.folder = new File(Platform.getLocation().toOSString() + this.project.getFullPath().toString());
		this.mapping = RepositoryMapping.getMapping(new Path(folder.getAbsolutePath()));
	}

	public void execute(Shell shell) {
		IWorkbenchSiteProgressService progress = PlatformUI.getWorkbench()
				.getService(IWorkbenchSiteProgressService.class);
		if (this.mapping == null)
			return;
		final Repository repo = this.mapping.getRepository();

		Job job = new Job(this.project.getName()) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					push(repo, monitor);
				} catch (InvocationTargetException | URISyntaxException | IOException | CoreException e) {
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
	}
	private void push(Repository repo, IProgressMonitor monitor) throws URISyntaxException,
	InvocationTargetException, IOException, CoreException {
		String url = prefs.getGitUrl();
		URIish uri = new URIish(url);
		StoredConfig config = repo.getConfig();
		RemoteConfig remoteConfig = new RemoteConfig(config, REMOTE_NAME);
		remoteConfig.addURI(uri);
		remoteConfig.update(config);
		config.save();
		//TODO: Use force push (true/false) ??
		PushOperation op = new PushOperation(repo, REMOTE_NAME, false, 0);
		op.run(monitor);
		remoteConfig.removeURI(uri);
		remoteConfig.update(config);
		config.save();
	}
}
