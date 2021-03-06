package com.clevercloud.eclipse.plugin.core;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import com.clevercloud.eclipse.plugin.api.CcApi;
import com.clevercloud.eclipse.plugin.api.json.logs.LogsSocketJSON;
import com.clevercloud.eclipse.plugin.ui.NotificationUI;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebSocketCore extends WebSocketClient {

	private String oauth;
	private String name;
	private boolean event;

	public WebSocketCore(URI uri, String name) throws NoSuchAlgorithmException, KeyManagementException {
		super(uri, new Draft_10(), null, 0);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, null, null);
		this.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sslContext));

		this.oauth = CcApi.getInstance().wsLogSigner();
		if (name == null)
			this.event = true;
		else
			this.event = false;
		this.name = name;
	}

	public void printSocket(String message) {
		if (ConsoleUtils.consoleExist(this.name))
			ConsoleUtils.printMessage(this.name, message);
		else
			this.close();
	}

	@Override
	public void onClose(int code, String msg, boolean remote) {
		if (ConsoleUtils.consoleExist(name))
			this.printSocket("Connection Closed");
		else
			System.out.println("Connetion Closed");
	}

	@Override
	public void onError(Exception e) {
		e.printStackTrace();
	}

	@Override
	public void onMessage(String message) {
		if (event) {
			NotificationUI.translateNotif(message);
		} else {
			ObjectMapper mapper = new ObjectMapper();
			try {
				LogsSocketJSON logs = mapper.readValue(message, LogsSocketJSON.class);
				this.printSocket(logs.getSource().getLog());
			} catch (IOException e) {
				this.printSocket("Error, bad json log");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		this.send(oauth);
		if (!event)
			this.printSocket("Connected");
	}
}
