package org.openhab.io.semantic.core;

public interface QueryResult {
	@Deprecated
	String getAsString();
	
	String getAsJsonString();

}
