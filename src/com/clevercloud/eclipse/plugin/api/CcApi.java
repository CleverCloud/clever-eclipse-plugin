package com.clevercloud.eclipse.plugin.api;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.clevercloud.eclipse.plugin.api.json.WebSocketJSON;
import com.clevercloud.eclipse.plugin.api.json.organisation.OrganisationJSON;
import com.clevercloud.eclipse.plugin.ui.LoginUI;
import com.clevercloud.eclipse.plugin.ui.NotificationUI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class CcApi {

	private static CcApi instance = null;

	private static final String API_KEY = "Pk2HtMWPXsBCbjZgkYRlfOJCvK7YXO";
	private static final String API_SECRET = "muu17xKYOSElHJB92OTnAAxYjgQhps";
	private static final String API_CALLBACK = "https://console.clever-cloud.com/cli-oauth";

	private static final String PREFS_TOKEN = "oauthToken";
	private static final String PREFS_SECRET = "oauthSecret";
	private static final String PREFS_USER = "username";
	private static final String PREFS_NODE = "com.clevercloud.eclipse.plugin.preferences";

	private OAuthService oauth = null;
	private String oauthVerifier = null;
	private String user = null;
	private Token accessToken = null;
	private boolean save = false;

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
		this.parseTokens(callbackUrl);
		Verifier verifier = new Verifier(this.oauthVerifier);
		this.accessToken = oauth.getAccessToken(requestToken, verifier);
		save = MessageDialog.openQuestion(shell, "Save session",
				"Would you like to save your user for the next session ?");

		NotificationUI.start();
	}

	public void parseTokens(String strurl) {
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
		deleteTokens();
	}

	public static void deleteTokens() {
		Preferences prefs = InstanceScope.INSTANCE.getNode(PREFS_NODE);
		prefs.remove(PREFS_TOKEN);
		prefs.remove(PREFS_SECRET);
		prefs.remove(PREFS_USER);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public void loadTokens() {
		Preferences prefs = InstanceScope.INSTANCE.getNode(PREFS_NODE);
		String token = prefs.get(PREFS_TOKEN, null);
		String secret = prefs.get(PREFS_SECRET, null);
		String user = prefs.get(PREFS_USER, null);

		if (token != null && secret != null && user != null) {
			System.out.println("Loading Tokens");
			Token accessToken = new Token(token, secret);
			OAuthRequest request = new OAuthRequest(Verb.GET, CleverCloudApi.BASE_URL + "/organisations?user=" + user);
			oauth.signRequest(accessToken, request);
			String org = request.send().getBody();

			ObjectMapper mapper = new ObjectMapper();
			try {
				mapper.readValue(org, OrganisationJSON[].class);
				this.accessToken = accessToken;
				this.user = user;
				this.save = true;
			} catch (IOException e) {
				System.out.println("Invalid Tokens");
			}
		}
		deleteTokens();
	}

	public void saveTokens() {
		if (isAuthentified() && this.save == true) {
			Preferences prefs = InstanceScope.INSTANCE.getNode(PREFS_NODE);
			prefs.put(PREFS_TOKEN, this.accessToken.getToken());
			prefs.put(PREFS_SECRET, this.accessToken.getSecret());
			prefs.put(PREFS_USER, this.getUser());
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
	}

	public String getUser() {
		return this.user;
	}

	public String apiGet(String url) {
		OAuthRequest request = new OAuthRequest(Verb.GET, CleverCloudApi.BASE_URL + url);
		this.oauth.signRequest(this.accessToken, request);
		Response response = request.send();
		return response.getBody();
	}

	public String apiPost(String url, Map<String, String> params) {
		OAuthRequest request = new OAuthRequest(Verb.POST, CleverCloudApi.BASE_URL + url);

		if (params != null)
			for (Map.Entry<String, String> entry : params.entrySet())
				request.addBodyParameter(entry.getKey(), entry.getValue());
		request.addHeader("Content-Type", "application/json");

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
		OAuthRequest request = new OAuthRequest(Verb.GET, CleverCloudApi.BASE_URL);
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
		return (orga.startsWith("orga_") ? "/organisations/" + orga : "/self");
	}
}
