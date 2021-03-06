package com.clevercloud.eclipse.plugin.api.json.organisation;

import java.io.IOException;

import com.clevercloud.eclipse.plugin.api.CcApi;
import com.clevercloud.eclipse.plugin.api.json.CleverServiceJSON;
import com.clevercloud.eclipse.plugin.api.json.application.ApplicationJSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown=true)
public class OrganisationJSON extends CleverServiceJSON {

	public ApplicationJSON[] getChilds() {
		if (!CcApi.isAuthentified())
			return null;
		ObjectMapper mapper = new ObjectMapper();
		String json = CcApi.getInstance().apiGet(CcApi.getOrgaUrl(this.getId()) + "/applications");
		ApplicationJSON[] childs = null;
		try {
			childs = mapper.readValue(json, ApplicationJSON[].class);
			for (ApplicationJSON app : childs) {
				app.setParent(this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return childs;
	}
}