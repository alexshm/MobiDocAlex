package projections.ScriptingLayer;

/**
 * intergace for Scripting languge
 */
public interface Iscript {

    public void init();

    public void makeProjection();

    void runScript(String scriptToRun);

}
