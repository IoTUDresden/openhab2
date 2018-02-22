package org.openhab.io.semantic.dogont.internal.performance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.StateDescription;
import org.eclipse.smarthome.core.types.StateOption;

public class PerformanceItem implements Item {
    private String name;

    public PerformanceItem(String name) {
        this.name = name;
    }

    @Override
    public State getState() {
        return new StringType();
    }

    @Override
    public State getStateAs(Class<? extends State> typeClass) {
        return new StringType();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "";
    }

    @Override
    public List<Class<? extends State>> getAcceptedDataTypes() {
        return new ArrayList<>();
    }

    @Override
    public List<Class<? extends Command>> getAcceptedCommandTypes() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getGroupNames() {
        return new ArrayList<>();
    }

    @Override
    public Set<String> getTags() {
        return new HashSet<>();
    }

    @Override
    public String getLabel() {
        return "";
    }

    @Override
    public boolean hasTag(String tag) {
        return false;
    }

    @Override
    public String getCategory() {
        return "";
    }

    @Override
    public StateDescription getStateDescription() {
        return new StateDescription(BigDecimal.valueOf(0.0), BigDecimal.valueOf(1.0), BigDecimal.valueOf(1.0), "",
                false, new ArrayList<StateOption>());
    }

    @Override
    public StateDescription getStateDescription(Locale locale) {
        return new StateDescription(BigDecimal.valueOf(0.0), BigDecimal.valueOf(1.0), BigDecimal.valueOf(1.0), "",
                false, new ArrayList<StateOption>());
    }

    @Override
    public String getUID() {
        return "UID_for_" + name;
    }

}
