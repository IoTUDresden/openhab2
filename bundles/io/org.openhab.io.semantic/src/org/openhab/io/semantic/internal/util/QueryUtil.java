package org.openhab.io.semantic.internal.util;

import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Contains some static helper methods for executing SPARQL querys
 * 
 * @author André Kühnert
 */
public class QueryUtil {
	private static final String URI_TAG = "<uri>";
	private static final String URI_TAG_END = "</uri>";	
	
	private QueryUtil() {
		// no need for a instance of this
	}
	
	/**
	 * Gets the subject by property value uncertainty.
	 *
	 * @param property the property
	 * @param namespace the namespace
	 * @param value the value
	 * @param deviceOnt the device ont
	 * @return the subject by property value uncertainty - null if not found
	 */
	public static String getSubjectByPropertyValueUncertainty(String property,
			String namespace, String value, OntModel deviceOnt) {
		return getSubjectByPropertyValue(property, namespace, value, deviceOnt, 
				QueryResource.SubjectByPropertyValueUncertainty);
	}
	
	/**
	 * Gets the subject by property value.
	 *
	 * @param property the property
	 * @param namespace the namespace
	 * @param value the value
	 * @param deviceOnt the device ont
	 * @return the subject by property value - null if not found
	 */
	public static String getSubjectByPropertyValue(String property,
			String namespace, String value, OntModel deviceOnt) {		
		return getSubjectByPropertyValue(property, namespace, value, deviceOnt, 
				QueryResource.SubjectByPropertyValue);
	}
	
	public static String getSubjectByStateValuePhaseId(String phaseId, OntModel model){
		String queryString = String.format(QueryResource.SubjectByPhaseId, phaseId);
		return phaseIdQuery(queryString, model);
	}
	
	public static String getStateValueByStateValuePhaseId(String phaseId, OntModel model){
		String queryString = String.format(QueryResource.StateValueByPhaseId, phaseId);
		return phaseIdQuery(queryString, model);
	}
	
	public static void getBuildingThingsWithStateValues(OntModel model){
		Query query = QueryFactory.create(QueryResource.BuildingThingsContainingStateValue);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();
		
		Resource stateResource = null;
		Resource valueResource = null;
		String localName = null;
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			stateResource = qs.getResource("state");
			valueResource = qs.getResource("value");
			localName = stateResource.getLocalName();
		}		
	}
	
	private static void addLocalNameAndValueResourceToMap(Map<String, Resource> map){
		
	}
	
	private static String phaseIdQuery(String queryString, OntModel model){
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();
		if(results.getRowNumber() != 1){
			qe.close();
			return null;
		}
		String resultXMLString = ResultSetFormatter.asXMLString(results);
		qe.close();
		return removeUriTag(resultXMLString);		
	}
	
	private static String getSubjectByPropertyValue(String property,
			String namespace, String value, OntModel deviceOnt, String queryStringUnformated){
		String queryString = String.format(queryStringUnformated, namespace, property, value);		
		Query query = QueryFactory.create(queryString);

		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, deviceOnt);
		ResultSet results = qe.execSelect();

		// Output query results
		String resultXMLString = ResultSetFormatter.asXMLString(results);
		qe.close();				
		return removeUriTag(resultXMLString);
	}
	
	private static String removeUriTag(String resultXMLString){		
		if (!resultXMLString.contains(URI_TAG))
			return null;
		int startURI = resultXMLString.indexOf(URI_TAG) + URI_TAG.length();
		int stopURI = resultXMLString.indexOf(URI_TAG_END);
		return resultXMLString.substring(startURI, stopURI);
	}

}
