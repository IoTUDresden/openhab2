package org.openhab.io.semantic.internal;

import java.io.ByteArrayOutputStream;

import org.openhab.io.semantic.core.QueryResult;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class QueryResultImpl implements QueryResult {
	private ByteArrayOutputStream jsonOutStream;
	
	public QueryResultImpl(ResultSet resultSet) {
		processResults(resultSet);
	}

	private void processResults(ResultSet resultSet) {
		jsonOutStream = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(jsonOutStream, resultSet);		
	}

	@Override
	public String getAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsJsonString() {
		return jsonOutStream.toString();
	}
}
