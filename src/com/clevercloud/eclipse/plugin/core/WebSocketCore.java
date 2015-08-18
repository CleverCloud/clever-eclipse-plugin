package com.clevercloud.eclipse.plugin.core;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import com.clevercloud.eclipse.plugin.api.CcApi;

public class WebSocketCore extends WebSocketClient {

	private String oauth;

	public WebSocketCore(URI uri) throws NoSuchAlgorithmException, KeyManagementException {
		super(uri, new Draft_10(), null, 0);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, null, null);
		this.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sslContext));

		this.oauth = CcApi.getInstance().wsLogSigner();
	}

	@Override
	public void onClose(int code, String msg, boolean remote) {
		System.out.println("Disconnected");
	}

	@Override
	public void onError(Exception arg0) {
		System.out.println("Error");
		arg0.printStackTrace();
	}

	@Override
	public void onMessage(String arg0) {
		System.out.println(arg0);
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		System.out.println("Connected");
		this.send(oauth);
	}
}
