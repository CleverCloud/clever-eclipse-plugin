package com.clevercloud.eclipse.plugin.api.json.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationInstanceJSON {

	private String type;

	public String getType() {
		return type;
	}
}
