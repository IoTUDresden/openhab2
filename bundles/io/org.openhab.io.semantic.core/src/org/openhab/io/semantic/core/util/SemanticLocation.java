package org.openhab.io.semantic.core.util;

public final class SemanticLocation {
    private String semanticUri;
    private String realLocationName;

    public SemanticLocation(String semanticUri, String realLocationName) {
        this.semanticUri = semanticUri;
        this.realLocationName = realLocationName;
    }

    public String getSemanticUri() {
        return semanticUri;
    }

    public void setSemanticUri(String semanticUri) {
        this.semanticUri = semanticUri;
    }

    public String getRealLocationName() {
        return realLocationName;
    }

    public void setRealLocationName(String realLocationName) {
        this.realLocationName = realLocationName;
    }

}
