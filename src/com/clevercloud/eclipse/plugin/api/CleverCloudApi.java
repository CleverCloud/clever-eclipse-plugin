package com.clevercloud.eclipse.plugin.api;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.clevercloud.eclipse.plugin.ui.LoginUI;

public class CleverCloudApi extends DefaultApi10a {

	public static final String BASE_URL = "https://api.clever-cloud.com/v2";
	private static final String LOGS_URL = "https://logs-api.clever-cloud.com/logs/";
	private static final String AUTHORIZE_URL = BASE_URL + "/oauth/authorize?oauth_token=%s";

	private static final String API_KEY = "T8NZNyJejYkevYvKj1EWgSq0rnXABH";
	private static final String API_SECRET = "QCLYg7n9YJwlxoEI0HkMSwetuphT9Q";
	private static final String CALLBACK_URL =  "https://console.clever-cloud.com/cli-oauth";

	public static String user = null;
	private static String oauthToken = null;
	private static String oauthVerifier = null;
	private static Token accessToken = null;
	private static OAuthService oauth = null;

	@Override
	public String getAccessTokenEndpoint() {
		return BASE_URL + "/oauth/access_token";
	}

	@Override
	public String getRequestTokenEndpoint() {
		return BASE_URL + "/oauth/request_token";
	}

	@Override
	public String getAuthorizationUrl(Token requestToken) {
		return String.format(AUTHORIZE_URL, requestToken.getToken());
	}

	public static void loadApi() {
		CleverCloudApi.oauth = new ServiceBuilder()
				.provider(CleverCloudApi.class)
				.apiKey(API_KEY)
				.apiSecret(API_SECRET)
				.callback(CALLBACK_URL)
				.build();
	}

	public static void executeLogin(Shell shell) {
		Token requestToken = CleverCloudApi.oauth.getRequestToken();
		String authURL = CleverCloudApi.oauth.getAuthorizationUrl(requestToken);
		LoginUI login = new LoginUI(shell, authURL);
		login.openLogin();

		if (CleverCloudApi.oauthVerifier == null)
			return;
		Verifier verifier = new Verifier(CleverCloudApi.oauthVerifier);
		CleverCloudApi.accessToken = CleverCloudApi.oauth.getAccessToken(requestToken, verifier);
	}

	public static void saveTokens(String strurl) {
		try {
			URL url = new URL(strurl);
			String[] datas = url.getQuery().split("&");
			for (String data : datas) {
				data.replace("?", "");
				if (data.startsWith("oauth_token=")) {
					oauthToken = data.split("=")[1];
				}
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
		if (CleverCloudApi.accessToken == null)
			return false;
		return true;
	}

	public static void disconnect() {
		CleverCloudApi.accessToken = null;
		Browser.clearSessions();
	}

	public static String apiRequest(String url) {
		OAuthRequest request = new OAuthRequest(Verb.GET, BASE_URL + url);
		oauth.signRequest(accessToken, request);
		Response response = request.send();
		return response.getBody();
	}

	public static String logRequest(String appid, Integer limit) {
		OAuthRequest request = new OAuthRequest(Verb.GET, LOGS_URL + appid + "?limit=" + limit.toString());
		oauth.signRequest(accessToken, request);
		Response response = request.send();
		return response.getBody();
	}

	public static String getOrgaUrl(String orga) {
		return (orga.equals("self") ? "/" : "/organisations/") + orga;
	}
}