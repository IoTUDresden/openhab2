package org.openhab.io.semantic.dogont;

import java.io.ByteArrayOutputStream;

import org.openhab.io.semantic.core.util.QueryResult;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;

/**
 * Implementation for the QueryResult.
 *
 * @author André Kühnert
 *
 */
public class QueryResultImpl implements QueryResult {
    private ByteArrayOutputStream jsonOutStream;

    /**
     * Generates a QueryResult from a Jena ResultSet. The ResultSet is completely read. After that,
     * it cant iterated again. If you want to reuse the ResultSet use
     * {@link #QueryResultImpl(ResultSetRewindable)}.
     * 
     * @param resultSet
     */
    public QueryResultImpl(ResultSet resultSet) {
        processResults(resultSet);
    }

    /**
     * Generates a QueryResult from a Jena ResultSet. The ResultSet is completely read and
     * afterwards reset. So you can reuse the result set and iterate over the results from the
     * beginning.
     * 
     * @param rewindableResultSet
     */
    public QueryResultImpl(ResultSetRewindable rewindableResultSet) {
        processResults(rewindableResultSet);
        rewindableResultSet.reset();
    }

    private void processResults(ResultSet resultSet) {
        jsonOutStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(jsonOutStream, resultSet);
    }

    @Override
    public String getAsJsonString() {
        return jsonOutStream.toString();
    }
}
