package org.openhab.binding.kodi.internal.util;

import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response.CompleteListener;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.openhab.binding.kodi.internal.methods.JsonRpc;
import org.openhab.binding.kodi.internal.methods.KodiGui;
import org.openhab.binding.kodi.internal.methods.KodiJsonRpc;
import org.openhab.binding.kodi.internal.methods.KodiPlayer;
import org.openhab.binding.kodi.internal.methods.KodiPlayer.PlayerOpenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//For post
//	http://<your-ip>:<your-port>/jsonrpc
//For get
//	http://<your-ip>:<your-port>/jsonrpc?request=<url-encoded-request>

//http://kodi.wiki/view/JSON-RPC_API

/**
 * Kodi remote, to control Kodi via the JSON-RPC API. This Remote uses a jetty client for accessing
 * Kodi via http.
 * 
 * @author André Kühnert
 *
 */
public class KodiRemote {
	private static final Logger logger = LoggerFactory.getLogger(KodiRemote.class);

	// Default authentication
	private static final String defaultAuth = "kodi:kodi";
	private static final String jsonRpc = "jsonrpc";

	private final String auth;
	private final String serverBaseUri;
	private final String creds;

	private HttpClient client;

	/**
	 * This creates a new Kodi Remote. The Default Passwort and Username is used.
	 * 
	 * @param serverBaseUri
	 * @throws Exception
	 */
	public KodiRemote(String serverBaseUri) throws Exception {
		this(serverBaseUri, null, null);
	}

	/**
	 * This creates a new Kodi Remote. The given username and password is used.
	 * 
	 * @param serverBaseUri
	 * @param username
	 * @param password
	 * @throws Exception
	 */
	public KodiRemote(String serverBaseUri, String username, String password) throws Exception {
		if (username == null || password == null)
			auth = defaultAuth;
		else
			auth = username.concat(":").concat(password);
		this.serverBaseUri = serverBaseUri;
		client = new HttpClient();
		byte[] out = Base64.getEncoder().encode(auth.getBytes());
		creds = new String(out);
		client.start();
	}

	/**
	 * Sends a small notification to Kodi
	 * 
	 * @param title
	 * @param message
	 * @param displayTime
	 *            how long should the message display (milliseconds)
	 */
	public void sendNotification(String title, String message, int displayTime) {
		 KodiGui.ShowNotification notification = new KodiGui.ShowNotification(title, message, displayTime);
		 postKodiJsonRpcAsync(notification);
	}

	/**
	 * Path of a folder, or the item (movie, sound file, picture)
	 */
	public void open(String itemPath, PlayerOpenType type) {
		 KodiPlayer.Open open = new KodiPlayer.Open(itemPath, type);
		 postKodiJsonRpcAsync(open);
	}

	/**
	 * Stops the playing of music, videos or the diashow
	 * 
	 * @param playerId
	 *            e.g. 1
	 */
	public void stop(int playerId) {
		 KodiPlayer.Stop stop = new KodiPlayer.Stop(playerId);
		 postKodiJsonRpcAsync(stop);
	}

	/**
	 * Sends a KodiJsonRpc and return the response.
	 * 
	 * @param kodiJsonRpc
	 * @return
	 */
	public ContentResponse postKodiJsonRpc(KodiJsonRpc kodiJsonRpc) {
		logger.debug("JSON-RPC: {}", kodiJsonRpc.getAsJsonString());
		ContentProvider content = new StringContentProvider(kodiJsonRpc.getAsJsonString());
		ContentResponse response = null;

		try {
			Request rq = client.POST(serverBaseUri)
					.path(jsonRpc)
					.header("Authorization", "Basic " + creds)
					.content(content, "application/json");
			logger.debug("Reques: {}", rq.toString());
			response = rq.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
		logger.debug("JSON-RPC Response Status: '{}'", response.getStatus());
		logger.debug("Response: {}", response.getContentAsString());
		return response;
	}

	/**
	 * Posts a async jsonRpc to Kodi
	 * 
	 * @param kodiJsonRpc
	 */
	public void postKodiJsonRpcAsync(KodiJsonRpc kodiJsonRpc) {
		logger.debug("JSON-RPC: {}", kodiJsonRpc.getAsJsonString());
		ContentProvider content = new StringContentProvider(kodiJsonRpc.getAsJsonString());

		client.POST(serverBaseUri)
		.path(jsonRpc)
		.header("Authorization", "Basic ".concat(creds))
		.content(content, "application/json")
		.send(new CompleteListener() {
					@Override
					public void onComplete(Result result) {
						logger.debug("Async Response Status for Kodi Method '{}': {}",
								kodiJsonRpc.getMethodName(), result.getResponse().getStatus());
					}
				});
	}

	public KodiJsonRpcVersion getJsonRpcVersion() {
		JsonRpc.Version rpc = new JsonRpc.Version();
		 ContentResponse response = postKodiJsonRpc(rpc);		 
		 String content = response.getContentAsString();
		 JsonObject jsonResponse = new JsonParser().parse(content).getAsJsonObject();
		 JsonObject json = jsonResponse.getAsJsonObject("properties");
		 int minor = json.get("minor").getAsInt();
		 int patch = json.get("patch").getAsInt();
		 int major = json.get("major").getAsInt();
		 KodiJsonRpcVersion version = new KodiJsonRpcVersion(minor, patch, major);
		 return version;
	}

	/**
	 * Stop this Client
	 * @throws Exception 
	 */
	public void stop() throws Exception {
		 client.stop();
	}

}
