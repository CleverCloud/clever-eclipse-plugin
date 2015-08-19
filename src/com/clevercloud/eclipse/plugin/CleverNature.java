package com.clevercloud.eclipse.plugin;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class CleverNature implements IProjectNature {

	public static final String NATURE_ID = "clever-eclipse.clevernature";
	private IProject project;

	@Override
	public void configure() throws CoreException {
	}

	@Override
	public void deconfigure() throws CoreException {
	}

	@Override
	public IProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	public static void addProjectNature(IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectDescription desc = project.getDescription();
		String[] natures = desc.getNatureIds();
		if (ArrayUtils.contains(natures, CleverNature.NATURE_ID))
			return;
		natures = ArrayUtils.add(natures, CleverNature.NATURE_ID);
		desc.setNatureIds(natures);
		project.setDescription(desc, monitor);
	}
}
