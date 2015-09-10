package com.clevercloud.eclipse.plugin.api.json.logs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LogsSocketJSON {

	@JsonProperty("_source")
	private LogsSocketSourceJSON source;

	public LogsSocketSourceJSON getSource() {
		return source;
	}
}
