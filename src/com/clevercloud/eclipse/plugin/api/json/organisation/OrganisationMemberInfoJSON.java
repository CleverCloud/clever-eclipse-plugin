package com.clevercloud.eclipse.plugin.api.json.organisation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class OrganisationMemberInfoJSON {

	private String id;
	private String email;
	private String name;

	public String getId() {
		return id;
	}

	public String getName() {
		if (name != null && name != "")
			return name;
		return email;
	}
}
