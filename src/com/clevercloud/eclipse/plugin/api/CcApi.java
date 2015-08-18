package com.clevercloud.eclipse.plugin.api;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.clevercloud.eclipse.plugin.api.json.WebSocketJSON;
import com.clevercloud.eclipse.plugin.ui.LoginUI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class CcApi {

	private static CcApi instance = null;

	private static final String API_KEY = "T8NZNyJejYkevYvKj1EWgSq0rnXABH";
	private static final String API_SECRET = "QCLYg7n9YJwlxoEI0HkMSwetuphT9Q";
	private static final String API_CALLBACK = "https://console.clever-cloud.com/cli-oauth";

	private OAuthService oauth = null;
	private String oauthVerifier = null;
	private String user = null;
	private Token accessToken = null;

	public static CcApi getInstance() {
		if (instance == null)
			instance = new CcApi();

		instance.initService();
		return instance;
	}

	private void initService() {
		if (oauth == null)
			oauth = new ServiceBuilder()
				.provider(CleverCloudApi.class)
				.apiKey(API_KEY)
				.apiSecret(API_SECRET)
				.callback(API_CALLBACK)
				.build();
	}

	public void executeLogin(Shell shell) {
		Token requestToken = this.oauth.getRequestToken();
		String authURL = this.oauth.getAuthorizationUrl(requestToken);
		LoginUI login = new LoginUI(shell, authURL);
		String callbackUrl = login.openLogin();

		if (callbackUrl == null)
			return;
		this.saveTokens(callbackUrl);
		Verifier verifier = new Verifier(this.oauthVerifier);
		this.accessToken = oauth.getAccessToken(requestToken, verifier);
	}

	public void saveTokens(String strurl) {
		try {
			URL url = new URL(strurl);
			String[] datas = url.getQuery().split("&");
			for (String data : datas) {
				data.replace("?", "");
				if (data.startsWith("oauth_verifier=")) {
					oauthVerifier = data.split("=")[1];
				}
				if (data.startsWith("user=")) {
					user = data.split("=")[1];
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static boolean isAuthentified() {
		if (instance == null)
			return false;
		if (getInstance().accessToken == null)
			return false;
		return true;
	}

	public static void disconnect() {
		instance = null;
		Browser.clearSessions();
	}

	public String getUser() {
		return this.user;
	}

	public String apiRequest(String url) {
		OAuthRequest request = new OAuthRequest(Verb.GET, CleverCloudApi.BASE_URL + url);
		this.oauth.signRequest(this.accessToken, request);
		Response response = request.send();
		return response.getBody();
	}

	public String logRequest(String appid, Integer limit) {
		OAuthRequest request = new OAuthRequest(Verb.GET,
				CleverCloudApi.LOGS_URL + appid + "?limit=" + limit.toString());
		this.oauth.signRequest(this.accessToken, request);
		Response response = request.send();
		return response.getBody();
	}

	public String wsLogSigner() {
		OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.clever-cloud.com/v2");
		this.oauth.signRequest(this.accessToken, request);
		WebSocketJSON ws = new WebSocketJSON(request.getHeaders()
				.toString().replace("{Authorization=", "").replace("}", ""));
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
		StringWriter writer = new StringWriter();
		try {
			mapper.writeValue(writer, ws);
			return writer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getOrgaUrl(String orga) {
		return (orga.equals("self") ? "/" : "/organisations/") + orga;
	}
}
