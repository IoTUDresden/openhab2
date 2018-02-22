package org.openhab.io.semantic.core.internal.util;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple bean for receiving the post command via rest on the semantic layer.
 * 
 * @author André Kühnert
 */
@XmlRootElement
public class SemanticPostCommandBean {
	/**
	 * The query statement, which should select the functions
	 */
	@XmlElement public String statement;
	/**
	 * The command which should be send to the items
	 */
	@XmlElement public String command;
	/**
	 * Set to true if the state values for each individual in the semantic model should be updated, 
	 * before the query is executed
	 */
	@XmlElement public boolean withlatest;
}
