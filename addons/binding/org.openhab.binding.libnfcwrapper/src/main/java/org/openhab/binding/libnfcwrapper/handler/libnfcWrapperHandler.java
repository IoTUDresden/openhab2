/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.libnfcwrapper.handler;

import static org.openhab.binding.libnfcwrapper.libnfcWrapperBindingConstants.*;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.libnfcwrapper.connection.NfcConnection;
import org.libnfcwrapper.connection.NfcDevice;
import org.libnfcwrapper.connection.NfcException;
import org.libnfcwrapper.connection.NfcProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link libnfcWrapperHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 * 
 * @author ak - Initial contribution
 */
public class libnfcWrapperHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(libnfcWrapperHandler.class);
    
    private NfcConnection connection;
    private NfcDevice device;

	public libnfcWrapperHandler(Thing thing) {
		super(thing);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		try {
			openConnection();
		} catch (NfcException e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
			return;
		}
		device.initiatorInit();
		device.setProperty(NfcProperty.NP_INFINITE_SELECT, false);
		
		//TODO start polling an this point
	}
	
	@Override
	public void dispose() {
		super.dispose();
		connection.close();
	}
	

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
        if(channelUID.getId().equals(CHANNEL_IDSCAN_RESULT)) {
            // TODO: handle command
        }
	}
	
	private void openConnection() throws NfcException{
		connection = new NfcConnection();
		connection.open();
		device = connection.getNfcDevice();
		if(device == null)
			throw new NfcException("no nfc-device found");
	}
}
