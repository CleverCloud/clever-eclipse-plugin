package com.clevercloud.eclipse.plugin.api.json;

import java.io.IOException;

import com.clevercloud.eclipse.plugin.api.CcApi;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown=true)
public class EventJSON {

	@JsonProperty("event")
	private String event;
	@JsonProperty("authorId")
	private String authorId;
	@JsonProperty("data")
	private String data;

	private String getAppName(EventDataJSON data) throws JsonParseException, JsonMappingException, IOException {
		if (data.getName() != null)
			return data.getName();
		ObjectMapper mapper = new ObjectMapper();
		String appJSON = CcApi.getInstance().apiRequest(CcApi.getOrgaUrl(data.getOwnerId()) + "/applications/"
		+ data.getAppId());

		ApplicationJSON app = mapper.readValue(appJSON, ApplicationJSON.class);
		return app.getName();
	}

	private String getOrgaName(String ownerId) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		if (ownerId.startsWith("user_"))
			return "";
		String organisationJSON = CcApi.getInstance().apiRequest("/organisations/" + ownerId);
		OrganisationJSON orga = mapper.readValue(organisationJSON, OrganisationJSON.class);
		return " in " + orga.getName();
	}

	private String getAuthorName(String ownerId) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		if (ownerId.startsWith("user_")) {
			String selfJSON = CcApi.getInstance().apiRequest("/self");
			SelfJSON self = mapper.readValue(selfJSON, SelfJSON.class);
			return self.getName();

		} else {
			String membersJSON = CcApi.getInstance().apiRequest("/organisations/" + ownerId +"/members");
			OrganisationMemberJSON[] members = mapper.readValue(membersJSON, OrganisationMemberJSON[].class);
			for (OrganisationMemberJSON member : members) {
				if (member.getInfos().getId().equals(this.authorId))
					return member.getInfos().getName();
			}
			return "";
		}
	}

	public String getMessage() {
		if (event == null)
			return null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (this.event.equals("APPLICATION_REDEPLOY")) {
				EventDataJSON data = mapper.readValue(this.data, EventDataJSON.class);
				return getAuthorName(data.getOwnerId()) + " redeployed "
						+ getAppName(data) + getOrgaName(data.getOwnerId());
			}

			if (this.event.equals("APPLICATION_STOP")) {
				EventDataJSON data = mapper.readValue(this.data, EventDataJSON.class);
				return getAuthorName(data.getOwnerId()) + " stopped "
						+ getAppName(data) + getOrgaName(data.getOwnerId());
			}

			if (this.event.equals("DEPLOYMENT_SUCCESS")) {
				EventDataJSON data = mapper.readValue(this.data, EventDataJSON.class);
				return getAppName(data) + " has been successfully deployed";
			}

			if (this.event.equals("DEPLOYMENT_FAIL")) {
				EventDataJSON data = mapper.readValue(this.data, EventDataJSON.class);
				return getAppName(data) + " has failed deploying";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
