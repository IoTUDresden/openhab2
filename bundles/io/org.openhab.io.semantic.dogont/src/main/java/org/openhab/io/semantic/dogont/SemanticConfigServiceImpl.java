package org.openhab.io.semantic.dogont;

import java.util.ArrayList;
import java.util.List;

import org.openhab.io.semantic.core.SemanticConfigService;
import org.openhab.io.semantic.core.util.Poi;
import org.openhab.io.semantic.core.util.QueryResult;
import org.openhab.io.semantic.core.util.SemanticLocation;
import org.openhab.io.semantic.core.util.SemanticPerson;
import org.openhab.io.semantic.core.util.SemanticThing;
import org.openhab.io.semantic.dogont.internal.SemanticConfigServiceImplBase;
import org.openhab.io.semantic.dogont.internal.util.QueryResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class SemanticConfigServiceImpl extends SemanticConfigServiceImplBase implements SemanticConfigService {
    private static final Logger logger = LoggerFactory.getLogger(SemanticConfigServiceImpl.class);

    @Override
    public List<SemanticThing> getSemanticThings() {
        // do a query on the semantic service to get all the
        // things with items and than build the list from the query
        // result
        List<SemanticThing> tmpList = new ArrayList<>();
        SemanticLocation l = new SemanticLocation("Dummy_Loc", "dummy living room");
        for (int i = 0; i < 10; i++) {
            SemanticThing t = new SemanticThing("Thing_Dummy_" + i, "OpenhabName_" + i, l, null);
            tmpList.add(t);
        }

        return tmpList;
    }

    @Override
    public void addPerson(SemanticPerson person) {
        // TODO complete
    }

    @Override
    public List<SemanticPerson> getSemanticPersons() {
        List<SemanticPerson> persons = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SemanticPerson p = new SemanticPerson("Testperson_" + i);
            p.setUid("UID_Testperson_" + i);
            persons.add(p);
        }
        return persons;
    }

    @Override
    public Poi getItemPoi(String itemName) {
        // TODO sparql query
        QueryResult qr = semanticService.executeSelect(QueryResource.ItemPoi(itemName));
        // Process json result

        return null;
    }

    public Poi getThingPoi(String thingPoi) {
        return null;
    }

    @Override
    public boolean updateItemPoi(String itemName, Poi newPoi) {
        String thingName = getThingNameForItem(itemName);
        return updateThingPoi(thingName, newPoi);
    }

    @Override
    public boolean updateThingPoi(String thingName, Poi newPoi) {
        if (newPoi == null || thingName == null) {
            logger.error("updating thing poi failed. no thingName or poi given");
            return false;
        }
        String queryString = QueryResource.UpdateThingPoi(thingName, newPoi);
        return semanticService.executeUpdate(queryString);
    }

    private String getThingNameForItem(String itemName) {
        if (itemName == null) {
            logger.error("updating thing poi failed. no itemName given");
            return null;
        }
        String queryString = String.format(QueryResource.GetThingWithFunctionOrState, itemName, itemName);
        QueryResult r = semanticService.executeSelect(queryString);
        return getThingNameFromQuery(r, itemName);
    }

    private String getThingNameFromQuery(QueryResult r, String itemName) {
        if (r == null) {
            logger.error("no thing found for item '{}'", itemName);
            return null;
        }

        JsonParser parser = new JsonParser();
        JsonObject el = parser.parse(r.getAsJsonString()).getAsJsonObject();
        JsonObject res = el.get("results").getAsJsonObject();
        JsonArray bind = res.get("bindings").getAsJsonArray();
        if (bind.size() < 1) {
            logger.error("no thing found for item '{}'", itemName);
            return null;
        }

        JsonObject first = bind.get(0).getAsJsonObject();
        String found = first.get("thing").getAsJsonObject().get("value").getAsString();
        return splitUri(found);
    }

    private String splitUri(String uri) {
        String[] split = uri.split("#");
        if (split.length == 2) {
            return split[1];
        }
        return null;
    }
}
