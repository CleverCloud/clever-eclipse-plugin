package com.clevercloud.eclipse.plugin.ui.wizards;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.clevercloud.eclipse.plugin.Activator;
import com.clevercloud.eclipse.plugin.api.json.CleverServiceJSON;
import com.clevercloud.eclipse.plugin.api.json.application.ApplicationJSON;

public class AppTreeLabelProvider implements ILabelProvider {

	private Image userIcon;
	private Image orgaIcon;
	private Image appIcon;

	public AppTreeLabelProvider() {
		userIcon = Activator.getImageDescriptor("icons/user_suit.png").createImage();
		orgaIcon = Activator.getImageDescriptor("icons/chart_organisation.png").createImage();
		appIcon = Activator.getImageDescriptor("icons/application_osx.png").createImage();
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
		this.userIcon.dispose();
		this.orgaIcon.dispose();
		this.appIcon.dispose();
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getImage(Object element) {
		CleverServiceJSON service = (CleverServiceJSON)element;
		if (service instanceof ApplicationJSON)
			return this.appIcon;
		if (service.getId().equals("self"))
			return this.userIcon;
		return this.orgaIcon;
	}

	@Override
	public String getText(Object element) {
		CleverServiceJSON service = (CleverServiceJSON)element;
		return service.getName();
	}
}
