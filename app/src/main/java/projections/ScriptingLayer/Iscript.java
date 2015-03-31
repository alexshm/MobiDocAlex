package projections.ScriptingLayer;

import projections.projection;

/**
 * intergace for Scripting languge
 */
public interface Iscript {

    public void init();

    public void makeProjection();

    void runScript(String scriptToRun, projection projection);

}
