package org.openhab.io.semantic.dogont;

import java.util.ArrayList;
import java.util.List;

import org.openhab.io.semantic.core.SemanticConfigService;
import org.openhab.io.semantic.core.util.Poi;
import org.openhab.io.semantic.core.util.QueryResult;
import org.openhab.io.semantic.core.util.SemanticHealthSensor;
import org.openhab.io.semantic.core.util.SemanticLocation;
import org.openhab.io.semantic.core.util.SemanticPerson;
import org.openhab.io.semantic.core.util.SemanticRobot;
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
    public boolean addPerson(SemanticPerson person) {
        if (person == null || person.getUid() == null || person.getUid().isEmpty()) {
            logger.error("cant update person. P is null or has no uid");
            return false;
        }
        return semanticService.executeUpdate(QueryResource.addPerson(person));
    }

    @Override
    public List<SemanticPerson> getSemanticPersons() {
        QueryResult result = semanticService.executeSelect(QueryResource.getPersons());
        List<SemanticPerson> persons = new ArrayList<>();
        createPersonsAndPutToList(persons, getBindingsArrayFromQuery(result));
        return persons;
    }

    private void createPersonsAndPutToList(List<SemanticPerson> list, JsonArray binds) {
        for (JsonElement jsonElement : binds) {
            String uid = jsonElement.getAsJsonObject().get("uid").getAsJsonObject().get("value").getAsString();

            // optionals
            String fName = getStringMemberFromJsonObject(jsonElement, "fName");
            String lName = getStringMemberFromJsonObject(jsonElement, "lName");
            String age = getStringMemberFromJsonObject(jsonElement, "age");
            String gender = getStringMemberFromJsonObject(jsonElement, "gender");
            String healthMonitorUid = getStringMemberFromJsonObject(jsonElement, "healthMonitorUid");

            SemanticPerson p = new SemanticPerson(uid, fName, lName, age, gender, healthMonitorUid);
            list.add(p);
        }
    }

    @Override
    public Poi getItemPoi(String itemName) {
        throw new UnsupportedOperationException("not implementet yet");
    }

    public Poi getThingPoi(String thingPoi) {
        throw new UnsupportedOperationException("not implementet yet");
    }

    @Override
    public boolean updateItemPoi(String itemName, Poi newPoi) {
        String thingName = getThingNameForItem(itemName);
        return updateThingPoi(thingName, newPoi);
    }

    @Override
    public boolean updateThingPoi(String thingName, Poi newPoi) {
        if (thingName == null) {
            logger.error("updating thing poi failed. no thingName given");
            return false;
        }
        String queryString = isEmptyPoi(newPoi) ? QueryResource.deleteThingPoi(thingName)
                : QueryResource.updateThingPoi(thingName, newPoi);

        return semanticService.executeUpdate(queryString);
    }

    private boolean isEmptyPoi(Poi poi) {
        return poi == null || poi.getPosition() == null || poi.getPosition().isEmpty() || poi.getOrientation() == null
                || poi.getOrientation().isEmpty();
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

    @Override
    public List<SemanticRobot> getSemanticRobots() {
        QueryResult result = semanticService.executeSelect(QueryResource.getAllRobots());
        List<SemanticRobot> robots = new ArrayList<>();
        createRobotsAndPutToList(robots, getBindingsArrayFromQuery(result));
        return robots;
    }

    private void createRobotsAndPutToList(List<SemanticRobot> list, JsonArray binds) {
        for (JsonElement jsonElement : binds) {
            String uid = jsonElement.getAsJsonObject().get("uid").getAsJsonObject().get("value").getAsString();
            // optionals
            String moveUid = getStringMemberFromJsonObject(jsonElement, "moveUid");
            String moveStateUid = getStringMemberFromJsonObject(jsonElement, "moveStateUid");
            String posStateUid = getStringMemberFromJsonObject(jsonElement, "posStateUid");

            SemanticRobot r = new SemanticRobot(uid, posStateUid, moveStateUid, moveUid);
            list.add(r);
        }
    }

    @Override
    public List<SemanticHealthSensor> getSemanticHealthSensors() {
        QueryResult result = semanticService.executeSelect(QueryResource.getAllHealthSensors());
        List<SemanticHealthSensor> sensors = new ArrayList<>();
        createHealthSensorsAndPutToList(sensors, getBindingsArrayFromQuery(result));
        return sensors;
    }

    private void createHealthSensorsAndPutToList(List<SemanticHealthSensor> list, JsonArray binds) {
        for (JsonElement jsonElement : binds) {
            String uid = jsonElement.getAsJsonObject().get("uid").getAsJsonObject().get("value").getAsString();
            // optionals
            String heartUid = getStringMemberFromJsonObject(jsonElement, "heartUid");
            String heartRateValue = getStringMemberFromJsonObject(jsonElement, "heartRateValue");
            String oxygenUid = getStringMemberFromJsonObject(jsonElement, "oxygenUid");
            String oxygenValue = getStringMemberFromJsonObject(jsonElement, "oxygenValue");
            SemanticHealthSensor s = new SemanticHealthSensor(uid, heartRateValue, oxygenValue, heartUid, oxygenUid);
            list.add(s);
        }
    }
}
