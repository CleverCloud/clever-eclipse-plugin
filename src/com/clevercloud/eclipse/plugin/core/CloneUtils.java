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
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.URIish;

import com.clevercloud.eclipse.plugin.CleverNature;
import com.clevercloud.eclipse.plugin.ui.NotificationUI;

@SuppressWarnings("restriction")
public class CloneUtils {

	private String name;
	private String id;
	private String url;
	private String orga;
	private String type;
	private IProgressMonitor monitor;
	private RepositoryUtil repositoryUtil;
	private IPreferenceStore store;

	public CloneUtils(String name, String url, String id, String orga, String type, IProgressMonitor monitor) {
		this.name = name;
		this.id = id;
		this.url = url;
		this.orga = orga;
		this.type = type;
		this.monitor = monitor;
		this.repositoryUtil = Activator.getDefault().getRepositoryUtil();
		this.store = org.eclipse.egit.ui.Activator.getDefault().getPreferenceStore();
	}

	public IStatus execute() throws URISyntaxException, InvocationTargetException,
									InterruptedException, CoreException {
		monitor.beginTask(this.name, 5);
		monitor.setTaskName("Creating repository");
		URIish uri = new URIish(this.url);
		File cloneDir = new File(Platform.getLocation().toOSString(), this.name);
		if (cloneDir.exists()) {
			NotificationUI.sendNotif("Can't clone " + name + ", folder already exists.");
			return Status.CANCEL_STATUS;
		}

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

			if (type.contains("php"))
				desc.setNatureIds(new String[] {CleverNature.PHP_NATURE, CleverNature.FACET_NATURE});
			if (type.contains("java"))
				desc.setNatureIds(new String[] {CleverNature.JAVA_NATURE});
			if (type.contains("ruby"))
				desc.setNatureIds(new String[] {CleverNature.RUBY_NATURE});
			if (type.contains("go"))
				desc.setNatureIds(new String[] {CleverNature.GO_NATURE});
			if (type.contains("sbt"))
				desc.setNatureIds(new String[] {CleverNature.SBT_NATURE, CleverNature.JAVA_NATURE});
			if (type.contains("node"))
				desc.setNatureIds(new String[] {CleverNature.NODE_NATURE, CleverNature.JS_NATURE,
						CleverNature.TERN_NATURE});

			project.setDescription(desc, monitor);
		}
		monitor.worked(1);

		monitor.setTaskName("Loading properties");
		PreferencesUtils prefs = new PreferencesUtils(project, false);
		prefs.setName(name);
		prefs.setGitUrl(url);
		prefs.setId(id);
		prefs.setOrga(orga);
		prefs.save();
		monitor.worked(1);

		monitor.setTaskName("Loading clever nature");
		CleverNature.addProjectNature(project, monitor);
		monitor.worked(1);
		monitor.done();
		NotificationUI.sendNotif(name + " cloned successfully.");
		return Status.OK_STATUS;
	}
}
