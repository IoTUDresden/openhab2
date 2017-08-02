package org.openhab.io.semantic.dogont.internal.performance;

import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.eclipse.smarthome.core.library.types.StringType;

public class PerformanceItemEvent extends ItemStateEvent {

    public PerformanceItemEvent(String itemName, String valueAsString) {
        super("", "", itemName, new StringType(valueAsString), "");

    }

}
