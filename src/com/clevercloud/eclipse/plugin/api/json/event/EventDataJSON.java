package com.clevercloud.eclipse.plugin.api.json.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class EventDataJSON {

	private String appId;
	private String ownerId;
	private String name;

	public String getAppId() {
		return this.appId;
	}

	public String getName() {
		return this.name;
	}

	public String getOwnerId() {
		return this.ownerId;
	}
}
