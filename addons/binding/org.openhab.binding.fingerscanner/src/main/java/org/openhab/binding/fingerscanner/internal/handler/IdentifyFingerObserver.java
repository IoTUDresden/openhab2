package org.openhab.binding.fingerscanner.internal.handler;

import org.openhab.binding.fingerscanner.internal.data.Person;


public interface IdentifyFingerObserver {
	
	void onSuccess(Person person);
	
	void onError(String errorMessage);
}
