package org.openhab.io.semantic.dogont.internal;

import org.openhab.io.semantic.core.SemanticService;

public abstract class SemanticConfigServiceImplBase {
    protected SemanticService semanticService;

    public void activate() {

    }

    public void deactivate() {

    }

    public void unsetSemanticService() {
        semanticService = null;
    }

    public void setSemanticService(SemanticService semanticService) {
        this.semanticService = semanticService;
    }

}
