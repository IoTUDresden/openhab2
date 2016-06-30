package org.openhab.io.semantic.dogont;

import java.util.ArrayList;
import java.util.List;

import org.openhab.io.semantic.core.SemanticConfigService;
import org.openhab.io.semantic.core.util.SemanticLocation;
import org.openhab.io.semantic.core.util.SemanticPerson;
import org.openhab.io.semantic.core.util.SemanticThing;
import org.openhab.io.semantic.dogont.internal.SemanticConfigServiceImplBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            SemanticThing t = new SemanticThing("Thing_Dummy_" + i, "OpenhabName_" + i, l);
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

}
