package org.openhab.io.semantic.core.internal.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.smarthome.core.items.GroupItem;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;

/**
 * The Config Helper generates a more readable output for all connected openhab items and things.
 *
 *
 * @author André Kühnert
 *
 */
public class ConfigHelper {
    private List<String> allreadyAddedItems = new ArrayList<>();
    private List<String> groupItems = new ArrayList<>();
    private StringBuilder out;

    public ConfigHelper() {
    }

    /**
     * Add things and items to this helper, to generate a more readable output, with all items and
     * things from openhab.
     *
     * @param thingRegistry
     * @param itemRegistry
     */
    public void addThingsAndItems(ThingRegistry thingRegistry, ItemRegistry itemRegistry) {
        out = new StringBuilder();
        allreadyAddedItems.clear();

        out.append(" Things:\n---------\n");
        for (Thing thing : thingRegistry.getAll()) {
            addThing(thing);
        }

        out.append(" \n\nItems which are not listed under a thing (e.g. items from the compat. layer):\n");
        out.append("------------------------------------------------------------------------------\n");
        for (Item item : itemRegistry.getAll()) {
            addItem(item);
        }

        out.append(" \n\nGroup Items:\n--------------\n");
        for (String group : groupItems) {
            out.append(group).append("\n");
        }
    }

    /**
     * Gets the a generated output with all things and items, connected to openhab.
     *
     * @return
     */
    public String getAsString() {
        return out.toString();
    }

    private void addThing(Thing thing) {
        ;
        String uid = thing.getUID().toString();
        uid = uid.replace(":", "_");
        out.append("\n");
        out.append(SemanticConstants.THING_PREFIX).append(uid).append("\n");
        out.append("    Functions:\n");
        appendFunctionsOrState(org.openhab.io.semantic.core.internal.util.SemanticConstants.FUNCTION_PREFIX, thing);
        out.append("    States:\n");
        appendFunctionsOrState(SemanticConstants.STATE_PREFIX, thing);
    }

    // TODO fix deprecation
    // - see how its done in
    // https://github.com/eclipse/smarthome/blob/master/bundles/core/org.eclipse.smarthome.core.thing/src/main/java/org/eclipse/smarthome/core/thing/dto/ChannelDTOMapper.java
    // - no fix in ESH so far
    private void appendFunctionsOrState(String stateOrFunctionPrefix, Thing thing) {
        Item tmpItem = null;
        for (Channel channel : thing.getChannels()) {

            // FIXME this can be removed, the resource are allocated automatically by the semantic layer
            /*
             * for (Iterator<Item> iterator = channel.getLinkedItems().iterator(); iterator.hasNext();) {
             * tmpItem = iterator.next();
             * out.append("        ").append(stateOrFunctionPrefix).append(tmpItem.getName()).append("\n");
             * out.append("                Type: ").append(tmpItem.getType()).append("\n");
             * allreadyAddedItems.add(tmpItem.getName());
             * }
             */
        }
    }

    private void addItem(Item item) {
        // item is listed under a thing
        if (allreadyAddedItems.contains(item.getName())) {
            return;
        }
        if (item instanceof GroupItem) {
            groupItems.add(item.getName());
        } else {
            out.append(item.getName()).append("\n");
            out.append("    Type: ").append(item.getType()).append("\n");
        }
    }

}
