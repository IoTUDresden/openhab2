package org.openhab.io.goal.core;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Goal {

    @XmlElement
    public String name;

}
