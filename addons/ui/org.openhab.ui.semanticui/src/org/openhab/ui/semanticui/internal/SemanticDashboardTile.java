package org.openhab.ui.semanticui.internal;

import org.openhab.ui.dashboard.DashboardTile;

public class SemanticDashboardTile implements DashboardTile {

    @Override
    public String getImageUrl() {
        // 350x185
        // TODO replace this, if images can be exposed from this bundle
        // at the moment images are located in
        // https://github.com/kaikreuzer/openhab-core/tree/master/bundles/org.openhab.ui.dashboard/web/img
        return "img/basicui.png";
    }

    @Override
    public String getName() {
        return "Semantic UI";
    }

    @Override
    public String getOverlay() {
        return "html5";
    }

    @Override
    public String getUrl() {
        return SemanticUIApp.WEBAPP_ALIAS + "/index.html";
    }

}
