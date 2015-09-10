package com.clevercloud.eclipse.plugin.api.json.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationDeploymentJSON {

	private String url;
	private String type;

	public String getUrl() {
		return this.url;
	}

	public String getType() {
		return this.type;
	}
}
