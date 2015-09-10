package com.clevercloud.eclipse.plugin.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class OrganisationMemberJSON {

	@JsonProperty("member")
	private OrganisationMemberInfoJSON member;

	public OrganisationMemberInfoJSON getInfos() {
		return member;
	}
}
