package org.openhab.io.semantic.tests;

import org.openhab.io.semantic.core.util.QueryResult;

public class QueryResultTestImpl implements QueryResult {
	
	private String result;
	
	public QueryResultTestImpl(String result) {
		this.result = result;
	}

	@Override
	public String getAsJsonString() {
		return result;
	}

}
