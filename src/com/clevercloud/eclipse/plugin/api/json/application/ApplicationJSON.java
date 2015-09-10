package com.clevercloud.eclipse.plugin.api.json.application;

import com.clevercloud.eclipse.plugin.api.json.CleverServiceJSON;
import com.clevercloud.eclipse.plugin.api.json.organisation.OrganisationJSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationJSON extends CleverServiceJSON {

	private ApplicationDeploymentJSON deployment;

	private OrganisationJSON parent;

	public ApplicationJSON() {
		this.parent = new OrganisationJSON();
		this.parent.setId("self");
	}

	public void setParent(OrganisationJSON parent) {
		this.parent = parent;
	}

	public OrganisationJSON getParent() {
		return this.parent;
	}

	public ApplicationDeploymentJSON getDeployment() {
		return this.deployment;
	}
}
