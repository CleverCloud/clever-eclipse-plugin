package com.clevercloud.eclipse.plugin.core;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.apache.commons.lang3.ArrayUtils;
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
import org.eclipse.egit.core.Activator;
import org.eclipse.egit.core.RepositoryUtil;
import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.URIish;

import com.clevercloud.eclipse.plugin.CleverNature;

@SuppressWarnings("restriction")
public class CloneUtils {

	private String name;
	private String url;
	private IProgressMonitor monitor;
	private RepositoryUtil repositoryUtil;
	private IPreferenceStore store;

	public CloneUtils(String name, String url, IProgressMonitor monitor) {
		this.name = name;
		this.url = url;
		this.monitor = monitor;
		this.repositoryUtil = Activator.getDefault().getRepositoryUtil();
		this.store = org.eclipse.egit.ui.Activator.getDefault().getPreferenceStore();
	}

	public IStatus execute() throws URISyntaxException, InvocationTargetException, InterruptedException, CoreException {
		monitor.beginTask(this.name, 3);
		monitor.setTaskName("Creating repository");
		URIish uri = new URIish(this.url);
		File cloneDir = new File(Platform.getLocation().toOSString(), this.name);
		if (cloneDir.exists())
			return Status.CANCEL_STATUS;

		CloneOperation op = new CloneOperation(uri, true, null, cloneDir, Constants.R_HEADS + Constants.MASTER,
				"clever", store.getInt(UIPreferences.REMOTE_CONNECTION_TIMEOUT));
		this.monitor.worked(1);

		monitor.setTaskName("Cloning repository");
		SubProgressMonitor sub = new SubProgressMonitor(monitor, 1);
		op.run(sub);
		sub.done();

		monitor.setTaskName("Importing repository");
		repositoryUtil.addConfiguredRepository(op.getGitDir());
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(this.name);
		File projectFile = new File(cloneDir.getAbsolutePath() + "/.project");
		IProjectDescription desc;
		if (projectFile.exists()) {
			desc = ResourcesPlugin.getWorkspace().loadProjectDescription(new Path(projectFile.getAbsolutePath()));
			desc.setNatureIds(ArrayUtils.add(desc.getNatureIds(), CleverNature.NATURE_ID));
			project.create(desc, monitor);
			project.open(monitor);
		} else {
			project.create(monitor);
			project.open(monitor);
			desc = project.getDescription();
			//TODO: Set langage plugin
			desc.setNatureIds(new String[] {JavaCore.NATURE_ID, CleverNature.NATURE_ID});
			project.setDescription(desc, monitor);
		}
		monitor.worked(1);
		monitor.done();
		return Status.OK_STATUS;
	}
}
