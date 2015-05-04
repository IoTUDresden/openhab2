package org.openhab.binding.fingerscanner.internal.client;

import java.util.ArrayList;
import java.util.List;

public class ClientManager {
	
	private List<FingerscanClient> clients = new ArrayList<FingerscanClient>();
	private List<String> ConnectedToServers = new ArrayList<String>();
	
	private static ClientManager cm;
	
	private ClientManager() {
	}
	
	public static synchronized ClientManager getInstance() {
		if (cm == null) {
			cm = new ClientManager();
		}
		return cm;
	}
	
	public List<FingerscanClient> getClients() {
		return clients;
	}

	public void setClients(List<FingerscanClient> clients) {
		this.clients = clients;
	}

	public List<String> getConnectedToServers() {
		return ConnectedToServers;
	}

	public void setConnectedToServers(List<String> connectedToServers) {
		ConnectedToServers = connectedToServers;
	}
	
	

}
