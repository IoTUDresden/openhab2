package org.openhab.binding.fingerscanner.internal.client;

import java.io.IOException;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import org.openhab.binding.fingerscanner.internal.handler.IdentifyFingerObserver;
import org.openhab.binding.fingerscanner.internal.handler.IdentifyFingerResultReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ocroquette.wampoc.adapters.jetty.JettyClient;
import fr.ocroquette.wampoc.client.WampClient;

public class FingerscanClient implements Observer {
	private static Logger logger = LoggerFactory.getLogger(FingerscanClient.class);
	
	private String uuid = UUID.randomUUID().toString();
	private String ip;
	private String port;
	private String name;
	private JettyClient client;
	private WampClient wclient;
	private IdentifyFingerObserver observer;
	
	public FingerscanClient(String name, String ip, String port) {
		this(name, ip, port, null);
	}
	
	public FingerscanClient(String name, String ip, String port, IdentifyFingerObserver observer){
		client = new JettyClient();
		this.name=name;
		this.ip=ip;
		this.port=port;
		this.observer = observer;
	}
		
	/**
	 * Connect to server
	 * @return
	 */	
	public boolean connect() {
		try {
			client.connect(URI.create("ws://"+ip+":"+port), null);
			ClientManager.getInstance().getClients().add(this);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		wclient = client.getWampClient();			
		return true;
	}
	
	public boolean disonnect(){
		wclient.reset();
		ClientManager.getInstance().getClients().remove(this);
//		try {
//			client.connect(URI.create(""), null);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		return true;
	}
	
	
	public void startIdentify(){
		logger.debug("start identify finger...");
		IdentifyFingerResultReceiver ifrh = new IdentifyFingerResultReceiver(observer);

		try {
			wclient.call("http://fingerscanner.org/identify/person", ifrh);
		} catch (IOException e) {
			logger.error("cant call remote procedure: {}", e.toString());
			e.printStackTrace();
		}
	}
	
	public String getUuid() {
		return uuid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void publish(String topic, Object payload) {
		try {
			wclient.publish(topic, payload);
			logger.debug("Sent: {}", payload);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	@Override
	public void update(Observable o, Object arg) {
		logger.debug("Update data: {} {}", arg, o);
	}
}
