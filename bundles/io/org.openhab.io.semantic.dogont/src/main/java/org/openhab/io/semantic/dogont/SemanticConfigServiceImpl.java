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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class SemanticConfigServiceImpl extends SemanticConfigServiceImplBase implements SemanticConfigService {
    private static final Logger logger = LoggerFactory.getLogger(SemanticConfigServiceImpl.class);

    @Override
    public List<SemanticThing> getSemanticThings() {
        QueryResult r = semanticService.executeSelect(QueryResource.getThings());
        return processThingsResult(r);
    }

    private List<SemanticThing> processThingsResult(QueryResult r) {
        List<SemanticThing> tmpL = new ArrayList<>();
        if (r == null) {
            logger.error("no things received in query");
            return tmpL;
        }

        JsonArray binds = getBindingsArrayFromQuery(r);
        createThingsAndPutToList(tmpL, binds);
        return tmpL;
    }

    private void createThingsAndPutToList(List<SemanticThing> list, JsonArray binds) {
        for (JsonElement jsonElement : binds) {
            String thing = jsonElement.getAsJsonObject().get("thing").getAsJsonObject().get("value").getAsString();
            String thingName = jsonElement.getAsJsonObject().get("thingName").getAsJsonObject().get("value")
                    .getAsString();
            String clazz = jsonElement.getAsJsonObject().get("class").getAsJsonObject().get("value").getAsString();

            // optional vars
            String loc = getStringMemberFromJsonObject(jsonElement, "loc");
            String realLoc = getStringMemberFromJsonObject(jsonElement, "realLoc");
            String locType = getStringMemberFromJsonObject(jsonElement, "locType");
            String position = getStringMemberFromJsonObject(jsonElement, "position");
            String orientation = getStringMemberFromJsonObject(jsonElement, "orientation");
            SemanticThing t = new SemanticThing(thing, thingName, clazz, new SemanticLocation(loc, realLoc, locType),
                    new Poi(position, orientation));
            list.add(t);
        }
    }

    // null if member not exists
    private String getStringMemberFromJsonObject(JsonElement object, String memberName) {
        if (!object.isJsonObject()) {
            return null;
        }
        JsonElement e = object.getAsJsonObject().get(memberName);
        if (e == null) {
            return null;
        }
        return e.getAsJsonObject().get("value").getAsString();
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
        QueryResult qr = semanticService.executeSelect(QueryResource.thingPoi(itemName));
        // Process json result

        return null;
    }

    public Poi getThingPoi(String thingPoi) {
        // TODO
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
        String queryString = QueryResource.updateThingPoi(thingName, newPoi);
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

        JsonArray bind = getBindingsArrayFromQuery(r);
        if (bind.size() < 1) {
            logger.error("no thing found for item '{}'", itemName);
            return null;
        }

        JsonObject first = bind.get(0).getAsJsonObject();
        String found = first.get("thing").getAsJsonObject().get("value").getAsString();
        return splitUri(found);
    }

    private JsonArray getBindingsArrayFromQuery(QueryResult r) {
        JsonParser parser = new JsonParser();
        JsonObject el = parser.parse(r.getAsJsonString()).getAsJsonObject();
        JsonObject res = el.get("results").getAsJsonObject();
        return res.get("bindings").getAsJsonArray();
    }

    private String splitUri(String uri) {
        String[] split = uri.split("#");
        if (split.length == 2) {
            return split[1];
        }
        return null;
    }

    @Override
    public List<SemanticLocation> getSemanticLocations() {
        QueryResult r = semanticService.executeSelect(QueryResource.getLocations());
        return processLocationResults(r);
    }

    private List<SemanticLocation> processLocationResults(QueryResult r) {
        List<SemanticLocation> tmpL = new ArrayList<>();
        JsonArray binds = getBindingsArrayFromQuery(r);
        if (binds == null) {
            logger.error("failed location query");
            return tmpL;
        }

        for (JsonElement jsonElement : binds) {
            String uri = getStringMemberFromJsonObject(jsonElement, "loc");
            String name = getStringMemberFromJsonObject(jsonElement, "realLoc");
            String clazz = getStringMemberFromJsonObject(jsonElement, "class");
            tmpL.add(new SemanticLocation(uri, name, clazz));
        }
        return tmpL;
    }

    @Override
    public SemanticLocation getSemanticLocationForThing(String thingName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean updateSemanticLocationForThing(String thingName, SemanticLocation location) {
        if (location == null || location.getSemanticUri() == null || location.getSemanticUri().isEmpty()) {
            String delQuery = QueryResource.deleteThingLocation(thingName);
            return semanticService.executeUpdate(delQuery);
        }
        String updateQuery = QueryResource.updateThingLocation(thingName, location.getSemanticUri());
        return semanticService.executeUpdate(updateQuery);
    }
}
