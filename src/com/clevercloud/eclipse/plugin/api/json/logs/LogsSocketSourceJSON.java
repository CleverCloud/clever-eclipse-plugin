package com.clevercloud.eclipse.plugin.api.json.logs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LogsSocketSourceJSON {

	@JsonProperty("message")
	private String message;

	@JsonProperty("@timestamp")
	private String timestamp;

	public String getLog() {
		return timestamp + ": " + message;
	}

	public String getMessage() {
		return message;
	}
}
