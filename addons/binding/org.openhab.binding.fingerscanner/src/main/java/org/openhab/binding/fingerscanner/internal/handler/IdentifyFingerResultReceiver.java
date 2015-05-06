package org.openhab.binding.fingerscanner.internal.handler;

import org.openhab.binding.fingerscanner.internal.data.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import fr.ocroquette.wampoc.client.RpcResultReceiver;

public class IdentifyFingerResultReceiver extends RpcResultReceiver {
	private static final Logger logger = LoggerFactory.getLogger(IdentifyFingerResultReceiver.class);
	private IdentifyFingerObserver observer;

	public IdentifyFingerResultReceiver() {
		this(null);
	}

	public IdentifyFingerResultReceiver(IdentifyFingerObserver observer) {
		this.observer = observer;
	}

	@Override
	public void onError() {
		String errorMessage = "Identify finger error";
		logger.error(errorMessage);
		if (observer != null)
			observer.onError(errorMessage);
	}

	@Override
	public void onSuccess() {
		logger.debug("Identify finger success");
		JsonElement obj = getPayload(JsonElement.class);

		if (obj instanceof JsonNull) {
			String message = "person not identified";
			logger.debug(message);
			if(observer != null)
				observer.onError(message);
			return;
		}

		Person person = getPerson();
		logger.debug("person '{}' with id '{}' identified", person.getName(), person.getId());
		if (observer != null)
			observer.onSuccess(person);
	}

	private Person getPerson() {
		JsonObject obj = getPayload(JsonObject.class);
		JsonElement nameElement = obj.get("name");
		JsonElement idElement = obj.get("id");
		String name = nameElement.getAsString();
		String id = idElement.getAsString();
		return new Person(id, name);
	}

}
