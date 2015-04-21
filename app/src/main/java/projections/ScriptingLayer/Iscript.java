package projections.ScriptingLayer;

import projections.projection;

/**
 * interface for Scripting Engine
 */
public interface Iscript {


    public void init();

    public projection buildProjection();

    void runScript(String scriptToRun, projection projection);

}
