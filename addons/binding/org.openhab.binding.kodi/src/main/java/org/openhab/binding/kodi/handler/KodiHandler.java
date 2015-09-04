/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.kodi.handler;

import static org.openhab.binding.kodi.KodiBindingConstants.*;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.kodi.internal.responses.KodiInfoLabels;
import org.openhab.binding.kodi.internal.responses.KodiJsonRpcVersion;
import org.openhab.binding.kodi.internal.util.KodiRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link KodiHandler} is responsible for handling commands, which are sent to one of the
 * channels.
 * 
 * @author Andre Kuehnert - Initial contribution
 */
public class KodiHandler extends BaseThingHandler {
	private Logger logger = LoggerFactory.getLogger(KodiHandler.class);

	private String ip = null;
	private int displayTime = 5000;
	private KodiRemote remote = null;
	private int refreshTime = 60;

	public KodiHandler(Thing thing) {
		super(thing);
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
		if (channelUID.getId().equals(CHANNEL_GUI_SHOW_NOTIFICATION_CHANNEL)) {
			if (!(command instanceof StringType)) {
				logger.error("The Kodi Item: {} supports only string commands",
						CHANNEL_GUI_SHOW_NOTIFICATION_CHANNEL);
				return;
			}
			remote.sendNotification("OpenHabMessage", command.toString(), displayTime);
			updateState(CHANNEL_GUI_SHOW_NOTIFICATION_CHANNEL, StringType.valueOf(command.toString()));
		}
	}

	@Override
	public void initialize() {
		setConfigValues();
		if(!startClient())
			return;	
		setDefaultValues();
		startBackgroundWorker();
		updateStatus(ThingStatus.ONLINE);
	}
	
	private boolean startClient(){
		try {
			remote = new KodiRemote(ip);
			KodiJsonRpcVersion version = remote.getJsonRpcVersion();
			logger.debug("Connected to Kodi. Version: {}", version.toString());
		} catch (Exception e) {
			logger.error("Cant connect to Kodi on '{}'", ip);
			updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
					"Can not connect to Kodi");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void startBackgroundWorker(){
		scheduler.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				updateInfos();
			}
		}, 5, refreshTime, TimeUnit.SECONDS);		
	}
	
	private void setDefaultValues(){
		updateState(CHANNEL_FRIENDLY_NAME, StringType.EMPTY);
		updateState(CHANNEL_UPTIME, StringType.EMPTY);
		updateState(CHANNEL_GUI_SHOW_NOTIFICATION_CHANNEL, StringType.EMPTY);	
	}
	
	private void setConfigValues(){
		ip = (String) getConfig().get(PARAMETER_IP);
		BigDecimal temp = (BigDecimal) getConfig().get(PARAMETER_DISPLAYTIME);
		displayTime = temp.intValue();
		temp = (BigDecimal) getConfig().get(PARAMETER_REFRESHTIME);
		refreshTime = temp.intValue();		
	}

	private void updateInfos() {
		KodiInfoLabels infos = remote.getKodiInfos("System.Uptime", "System.FriendlyName");
		updateState(CHANNEL_FRIENDLY_NAME, StringType.valueOf(infos.getInfo("System.FriendlyName")));
		updateState(CHANNEL_UPTIME, StringType.valueOf(infos.getInfo("System.Uptime")));
	}
}
