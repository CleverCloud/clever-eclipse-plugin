package com.clevercloud.eclipse.plugin.ui.wizards;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.clevercloud.eclipse.plugin.api.json.CleverServiceJSON;
import com.clevercloud.eclipse.plugin.api.json.application.ApplicationJSON;
import com.clevercloud.eclipse.plugin.api.json.organisation.OrganisationJSON;

public class AppTreeContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return (CleverServiceJSON[]) inputElement;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		CleverServiceJSON service = (CleverServiceJSON)parentElement;
		if (service instanceof OrganisationJSON) {
			OrganisationJSON orga = (OrganisationJSON)service;
			return orga.getChilds();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		CleverServiceJSON service = (CleverServiceJSON)element;
		if (service instanceof ApplicationJSON) {
			ApplicationJSON app = (ApplicationJSON)service;
			return app.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		CleverServiceJSON service = (CleverServiceJSON)element;
		if (service instanceof OrganisationJSON) {
			OrganisationJSON orga = (OrganisationJSON)service;
			if (orga.getChilds().length > 0)
				return true;
		}
		return false;
	}

}
