package unused.model.managers;

import unused.model.gateways.DatabaseGateway;

public interface Manager {

    // interface for all Managers, for use with Gateways

    /**
     * Exports Articles stored in the Manager. If exportAll is true, then exports all existing Articles; else exports new and updated Articles only.
     * @param gateway a gateway implementing DatabaseGateway
     * @param exportAll boolean determining whether to export all Articles or only new Articles
     */
    void export(DatabaseGateway gateway, boolean exportAll);
}
