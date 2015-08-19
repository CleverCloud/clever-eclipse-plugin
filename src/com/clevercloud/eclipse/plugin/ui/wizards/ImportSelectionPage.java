package com.clevercloud.eclipse.plugin.ui.wizards;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.clevercloud.eclipse.plugin.api.CcApi;
import com.clevercloud.eclipse.plugin.api.json.ApplicationJSON;
import com.clevercloud.eclipse.plugin.api.json.CleverServiceJSON;
import com.clevercloud.eclipse.plugin.api.json.OrganisationJSON;
import com.clevercloud.eclipse.plugin.api.json.SelfJSON;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ImportSelectionPage extends WizardPage {

	private Composite container;
	private TreeViewer treeViewer;

	private final static String  DESCRIPTION = "Import an existing project from Clever Cloud";

	public ImportSelectionPage() {
		super("Import Application");
		setTitle("Import Application");
		setDescription(DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		this.container = new Composite(parent, SWT.NONE);
		GridLayout lay = new GridLayout(1, false);
		this.container.setLayout(lay);

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			OrganisationJSON self = new OrganisationJSON();
			self.setId("self");
			SelfJSON selfInfo = objectMapper.readValue(CcApi.getInstance().apiRequest("/self"), SelfJSON.class);
			self.setName(selfInfo.getName());

			OrganisationJSON[] orgas = objectMapper.readValue(CcApi.getInstance()
					.apiRequest("/organisations?user=" + CcApi.getInstance().getUser()), OrganisationJSON[].class);
			orgas = ArrayUtils.add(orgas, self);
			this.createTree(orgas);

		} catch (IOException e) {
			e.printStackTrace();
		}
		setControl(container);
		setPageComplete(false);
	}

	private void createTree(OrganisationJSON[] orgas) {
		this.treeViewer = new TreeViewer(this.container);
		this.treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.treeViewer.setContentProvider(new AppTreeContentProvider());
		this.treeViewer.setLabelProvider(new AppTreeLabelProvider());
		this.treeViewer.setInput(orgas);

		this.treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setPageComplete(canFinish());
				CleverServiceJSON service = (CleverServiceJSON)((IStructuredSelection)event
						.getSelection()).getFirstElement();
				if (service instanceof ApplicationJSON && !canFinish()) {
					if (!((ApplicationJSON)service).getDeployment().getType().equals("GIT")) {
						setErrorMessage("This plugin can only work with git applications.");
					} else {
						setErrorMessage("Can't clone " + service.getName()
						+ ", it already exist in your workspace.");
					}
				} else {
					setErrorMessage(null);
				}
			}
		});
		this.treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				Object item = selection.getFirstElement();
				if (item instanceof ApplicationJSON) {
					if (canFinish())
						if (getWizard().performFinish())
							getWizard().getContainer().getShell().close();
				} else {
					if (treeViewer.getExpandedState(item)) {
						treeViewer.collapseToLevel(item, 1);
					} else {
						treeViewer.expandToLevel(item, 1);
					}
				}
			}
		});
	}

	private boolean canFinish() {
		IStructuredSelection selection = (IStructuredSelection)this.treeViewer.getSelection();
		if (selection.getFirstElement() instanceof ApplicationJSON) {
			ApplicationJSON app = (ApplicationJSON)selection.getFirstElement();
			File cloneDir = new File(Platform.getLocation().toOSString(), app.getName());
			if (!cloneDir.exists() && app.getDeployment().getType().equals("GIT")) {
				return true;
			}
		}
		return false;
	}

	public ApplicationJSON getSelectedItem() {
		IStructuredSelection selection = (IStructuredSelection)this.treeViewer.getSelection();
		return (ApplicationJSON)selection.getFirstElement();
	}
}
