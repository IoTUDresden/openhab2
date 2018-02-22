package org.openhab.io.semantic.dogont.internal.performance;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.StateDescription;

public class PerformanceItem implements Item {
    private String name;

    public PerformanceItem(String name) {
        this.name = name;
    }

    @Override
    public State getState() {
        return null;
    }

    @Override
    public State getStateAs(Class<? extends State> typeClass) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public List<Class<? extends State>> getAcceptedDataTypes() {
        return null;
    }

    @Override
    public List<Class<? extends Command>> getAcceptedCommandTypes() {
        return null;
    }

    @Override
    public List<String> getGroupNames() {
        return null;
    }

    @Override
    public Set<String> getTags() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public boolean hasTag(String tag) {
        return false;
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public StateDescription getStateDescription() {
        return null;
    }

    @Override
    public StateDescription getStateDescription(Locale locale) {
        return null;
    }

    @Override
    public String getUID() {
        return "UID_for_" + name;
    }

}
