/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fingerscanner.handler;

import static org.openhab.binding.fingerscanner.FingerScannerBindingConstants.CHANNEL_IDENTIFY;
import static org.openhab.binding.fingerscanner.FingerScannerBindingConstants.CHANNEL_PERSON;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.fingerscanner.FingerScannerBindingConstants;
import org.openhab.binding.fingerscanner.internal.client.FingerscanClient;
import org.openhab.binding.fingerscanner.internal.data.Person;
import org.openhab.binding.fingerscanner.internal.handler.IdentifyFingerObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FingerScannerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 * 
 * @author Andre Kuehnert - Initial contribution
 */
public class FingerScannerHandler extends BaseThingHandler {
    private Logger logger = LoggerFactory.getLogger(FingerScannerHandler.class);
    
	private FingerscanClient client;
	private IdentifyFingerObserver identifyFingerObserver;

	public FingerScannerHandler(Thing thing) {
		super(thing);
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
        if(channelUID.getId().equals(CHANNEL_IDENTIFY)) {
        	if(command.equals(OnOffType.ON)){
        		client.startIdentify();
        	}            	
        }
	}
	
	@Override
	public void initialize() {
		super.initialize();
		setObserver();
		client = new FingerscanClient("Fingerscan Client", "192.168.1.6", "7003", identifyFingerObserver);
		@SuppressWarnings("unused")
		boolean connected = client.connect();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		client.disonnect();
		client = null;
	}
	
	private void onIdentifySuccess(Person person){
		//TODO update the ui
		logger.info("Person Identfied: {}", person.getName());
		updateState(CHANNEL_PERSON, new StringType(person.getName()));
	}
	
	private void onIdentifyError(String errorMessage){
		//TODO update the ui
		logger.info("Person not identified: {}", errorMessage);
		updateState(CHANNEL_PERSON, new StringType(errorMessage));
	}
	
	private void setObserver(){
		identifyFingerObserver = new IdentifyFingerObserver() {			
			@Override
			public void onSuccess(Person arg0) {
				onIdentifySuccess(arg0);
			}			
			@Override
			public void onError(String arg0) {		
				onIdentifyError(arg0);
			}
		};		
	}
}
