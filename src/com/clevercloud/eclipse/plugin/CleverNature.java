package com.clevercloud.eclipse.plugin;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class CleverNature implements IProjectNature {

	public static final String NATURE_ID = "clever-eclipse.clevernature";

	public static final String PHP_NATURE = "org.eclipse.php.core.PHPNature";
	public static final String FACET_NATURE = "org.eclipse.wst.common.project.facet.core.nature";

	public static final String RUBY_NATURE = "org.eclipse.dltk.ruby.core.nature";

	public static final String GO_NATURE = "com.googlecode.goclipse.core.goNature";

	public static final String JAVA_NATURE = "org.eclipse.jdt.core.javanature";

	public static final String PYTHON_NATURE = "org.python.pydev.pythonNature";

	public static final String SBT_NATURE = "org.scala-ide.sdt.core.scalanature";

	public static final String NODE_NATURE = "org.nodeclipse.ui.NodeNature";
	public static final String JS_NATURE = "org.eclipse.wst.jsdt.core.jsNature";
	public static final String TERN_NATURE = "tern.eclipse.ide.core.ternnature";

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
