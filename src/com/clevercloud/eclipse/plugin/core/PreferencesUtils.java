package com.clevercloud.eclipse.plugin.core;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.clevercloud.eclipse.plugin.Activator;
import com.clevercloud.eclipse.plugin.api.CleverCloudApi;
import com.clevercloud.eclipse.plugin.api.json.ApplicationJSON;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PreferencesUtils {

	private ProjectScope projectScope;
	private IPreferenceStore store;
	private boolean autosave;

	private static final String GIT_KEY = "GIT_URL";
	private static final String ID_KEY = "ID";
	private static final String NAME_KEY = "NAME";
	private static final String ORGA_KEY = "ORGANISATION";

	public PreferencesUtils(IProject project, boolean autosave) {
		this.projectScope = new ProjectScope(project);
		this.store = new ScopedPreferenceStore(projectScope, Activator.PLUGIN_ID);
		this.autosave = autosave;
	}

	public String getGitUrl() {
		return store.getString(GIT_KEY);
	}

	public String getId() {
		return store.getString(ID_KEY);
	}

	public String getName() {
		return store.getString(NAME_KEY);
	}

	public String getOrga() {
		return store.getString(ORGA_KEY);
	}

	public void setGitUrl(String url) {
		store.setValue(GIT_KEY, url);
		if (this.autosave)
			this.save();
	}

	public void setId(String id) {
		store.setValue(ID_KEY, id);
		if (this.autosave)
			this.save();
	}

	public void setName(String name) {
		store.setValue(NAME_KEY, name);
		if (this.autosave)
			this.save();
	}

	public void setOrga(String orga) {
		store.setValue(ORGA_KEY, orga);
		if (this.autosave)
			this.save();
	}

	public boolean save() {
		try {
			((ScopedPreferenceStore)store).save();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean updateValues() {
		String orga = this.getOrga();
		String id = this.getId();
		String name = this.getName();
		String url = this.getGitUrl();
		ObjectMapper mapper = new ObjectMapper();
		String request = CleverCloudApi.apiRequest(CleverCloudApi.getOrgaUrl(orga) + "/applications/" + id);
		try {
			ApplicationJSON app = mapper.readValue(request, ApplicationJSON.class);
			if (!name.equals(app.getName()))
				this.setName(app.getName());
			if (!url.equals(app.getDeployment().getUrl()))
				this.setGitUrl(app.getDeployment().getUrl());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
