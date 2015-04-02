package projections.ScriptingLayer;

import projections.projection;

/**
 * intergace for Scripting languge
 */
public interface Iscript {


    public void init();

    public projection buildProjection();

    void runScript(String scriptToRun, projection projection);

}
