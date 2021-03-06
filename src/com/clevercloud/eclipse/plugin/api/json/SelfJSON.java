package com.clevercloud.eclipse.plugin.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SelfJSON {
	
	private String id;
	private String email;
	private String name;

	public String getId() {
		return this.id;
	}

	public String getEmail() {
		return this.email;
	}

	public String getName() {
		return this.name;
	}
}
