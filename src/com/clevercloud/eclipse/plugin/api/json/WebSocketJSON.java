package com.clevercloud.eclipse.plugin.api.json;

public class WebSocketJSON {

	private String message_type = "oauth";
	private String authorization;

	public WebSocketJSON(String authorization) {
		this.authorization = authorization;
	}

	public String getMessage_type() {
		return this.message_type;
	}

	public String getAuthorization() {
		return this.authorization;
	}
}
