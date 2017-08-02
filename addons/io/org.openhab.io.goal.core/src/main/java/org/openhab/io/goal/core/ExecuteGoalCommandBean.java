package org.openhab.io.goal.core;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openhab.io.semantic.core.util.SemanticLocation;

@XmlRootElement
public class ExecuteGoalCommandBean {

    @XmlElement
    public Goal goal;
    @XmlElement
    public List<Quality> qualities;
    @XmlElement
    public SemanticLocation location;

}
