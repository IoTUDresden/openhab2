/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.libnfcwrapper.handler;

import static org.openhab.binding.libnfcwrapper.LibnfcWrapperBindingConstants.*;

import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.libnfcwrapper.connection.NfcConnection;
import org.libnfcwrapper.connection.NfcDevice;
import org.libnfcwrapper.connection.NfcException;
import org.libnfcwrapper.connection.NfcProperty;
import org.libnfcwrapper.connection.NfcTarget;
import org.libnfcwrapper.connection.TargetFoundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link LibnfcWrapperHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 * 
 * @author André Kühnert - Initial contribution
 */
public class LibnfcWrapperHandler extends BaseThingHandler implements TargetFoundHandler {

    private Logger logger = LoggerFactory.getLogger(LibnfcWrapperHandler.class);
    
    private NfcConnection connection;
    private NfcDevice device;

	public LibnfcWrapperHandler(Thing thing) {
		super(thing);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		if(!openConnection()){
			updateStatus(ThingStatus.OFFLINE);
			return;
		}
		device.initiatorInit();
		device.setProperty(NfcProperty.NP_INFINITE_SELECT, false);
		scheduler.execute(new Runnable() {			
			@Override
			public void run() {
				runPolling();			
			}
		});
		updateStatus(ThingStatus.ONLINE);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		updateStatus(ThingStatus.OFFLINE);
		connection.close();
	}
	

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
        if(channelUID.getId().equals(CHANNEL_IDSCAN_RESULT)) {
            logger.warn("the libnfc wrapper does not handle commands");
        }
	}
	
	private boolean openConnection(){
		try {
			connection = new NfcConnection();
			connection.open();
		} catch (NfcException e) {
			logger.error("error while opening nfc connection");
			e.printStackTrace();
			return false;
		}
		device = connection.getNfcDevice();
		if(device == null){
			logger.debug("no device was found");
			return false;
		}
		return true;
	}

	@Override
	public void onError(String arg0) {
		logger.error("Error happened while polling for nfc target");
		updateStatus(ThingStatus.OFFLINE);
		
		//TODO check if that works
		if(!scheduler.isTerminated() || !scheduler.isShutdown())
			scheduler.shutdownNow();
	}

	@Override
	public void onTargetFound(NfcTarget arg0) {
		String uid = arg0.getUidAsString();
		updateState(CHANNEL_IDSCAN_RESULT, new StringType(uid));
	}
	
	//after the first polling the second polling is started
	private void runPolling(){
		device.poll(this);
		scheduler.execute(new Runnable() {			
			@Override
			public void run() {
				runPolling();				
			}
		});
	}
}
