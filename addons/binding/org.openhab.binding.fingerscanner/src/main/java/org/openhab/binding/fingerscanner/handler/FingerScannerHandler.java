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

import java.util.List;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.fingerscan.connection.FPrintConnection;
import org.fingerscan.connection.FPrintData;
import org.fingerscan.connection.FPrintDevice;
import org.fingerscan.connection.FPrintException;
import org.fingerscan.data.DbConnection;
import org.fingerscan.data.Person;
import org.fingerscan.util.PersonListUtil;
import org.libfprintwrapper.interfaces.FpCompareResult;
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
	
	//DebugLevel for the libfprint library
	private static final int DEBUG_LEVEL = 3;	
	
	private DbConnection dbConnection = null;
	private FPrintConnection fprintConnection = null;
	private FPrintDevice device = null;
	private Person person = null;
	private List<FPrintData> data = null;

	public FingerScannerHandler(Thing thing) {
		super(thing);
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
        if(channelUID.getId().equals(CHANNEL_IDENTIFY)) {
        	if(command.equals(OnOffType.ON)){
        		scan();
        	}            	
        }
        //dirty fix to switch the button back to off
        resetSwitch();
        resetSwitch();
	}
	
	@Override
	public void initialize() {
		super.initialize();
		updateStatus(ThingStatus.ONLINE);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	private void scan(){
		// first create dbconnection than the fprint connection
		if (!createDBConnection() || !createFPrintConnection()) {
			updateState(CHANNEL_PERSON, new StringType("ERROR"));
			return;
		}

		// scan the finger
		if (!identifyFinger()) {
			updateState(CHANNEL_PERSON, new StringType("not found"));
			cleanupAndCloseConnections();
			return;
		}

		// set person to output
		updateState(CHANNEL_PERSON, new StringType(person.getName()));

		// cleanup
		cleanupAndCloseConnections();
	}
	
	private void resetSwitch(){
		updateState(CHANNEL_IDENTIFY, OnOffType.OFF);	
	}
	
	private void cleanupAndCloseConnections() {
		PersonListUtil.freeAllFPrintData(data);
		data = null;
		dbConnection.close();
		dbConnection = null;
		fprintConnection.close();
		fprintConnection = null;
		device = null;
		person = null;
	}

	private boolean identifyFinger() {
		logger.debug("scan finger...");
		List<Person> persons = dbConnection.getPersons();
		data = PersonListUtil.getFPrintDataList(persons);
		FpCompareResult result = device.identifyPrintDataWithPointerArray(data);

		if (!result.matchFound()) {
			logger.debug("no match found");
			return false;
		}

		person = persons.get(result.matchOffset());
		logger.debug("match found: {}", person.getName());
		return true;
	}

	private boolean createFPrintConnection() {
		try {
			fprintConnection = new FPrintConnection();
			fprintConnection.setDebug(DEBUG_LEVEL);
			fprintConnection.open();
			device = fprintConnection.getDevice();
		} catch (FPrintException e) {
			logger.error("error while opening the connection to the fprint device");
			e.printStackTrace();
			dbConnection.close();
			fprintConnection = null;
			device = null;
			return false;
		}
		return true;
	}

	private boolean createDBConnection() {
		dbConnection = new DbConnection();
		try {
			dbConnection.open();
		} catch (ClassNotFoundException e) {
			logger.error("error while opening db connection for fprint");
			e.printStackTrace();
			dbConnection = null;
			return false;
		}
		return true;
	}
}
