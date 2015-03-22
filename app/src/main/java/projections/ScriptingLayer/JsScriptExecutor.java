package projections.ScriptingLayer;

import android.util.Log;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.*;

import projections.DataItem;
import projections.projection;
import projections.CyclicProjectionAbstract;
import projections.projection.ProjectionTimeUnit;
import org.mozilla.classfile.*;

/**
 * Created by Moshe on 3/18/2015.
 */



public class JsScriptExecutor {
//
        android.content.Context c;
    public JsScriptExecutor( android.content.Context cont)
    {
        c=cont;
    }
   public void init(){

    }



    private static final String DataItem = "var DataItem = " +
            "java.lang.Class.forName(\"" + JsScriptExecutor.class.getName() + "\", true, javaLoader);" +
            "var methodbuildProj = DataItem.getMethod(\"buildProj\", [java.lang.String]);" +
            "var proj = function (name) {return methodbuildProj.invoke(null, name);};";



    /* call java funcition from the script
    =======================================

    String script = "function abc(x,y) {return x+y;}"
        + "function def(u,v) {return u-v;}";
Context context = Context.enter();
try {
    ScriptableObject scope = context.initStandardObjects();
    context.evaluateString(scope, script, "script", 1, null);
    Function fct = (Function)scope.get("abc", scope);
    Object result = fct.call(
            context, scope, scope, new Object[] {2, 3});
    System.out.println(Context.jsToJava(result, int.class));
} finally {
    Context.exit();
}
========================================================
     */
    public   String readUrl(String url) {
        // Read the HTML text with HttpClient

        return url;
    }
    public projection buildProj(String name)
    {
        return new CyclicProjectionAbstract(name,c,"08:00");
    }
    public   void runScript(String scriptToRun) {


        scriptToRun=getScriptFromServer();
       Context context = Context.enter();
        context.setOptimizationLevel(-1);
        try {
            ScriptableObject scope = context.initStandardObjects();
            ScriptableObject.putProperty(scope, "javaLoader", Context.javaToJS(JsScriptExecutor.class.getClassLoader(), scope));
           // context.evaluateString(scope, "var global = {};", "global", 1, null);

          //  scope.put("projection", scope, projection.class);
          //  scope.put("cyclicProjection", scope, CyclicProjectionAbstract.class);
          //  scope.put("script", scope, scriptToRun);
            System.out.println("the script is : \n"+scriptToRun);
            context.evaluateString(scope, scriptToRun, "script", 1, null);
           // context.evaluateString(scope, "result = global.js_beautify(source);", "beautify", 1,null);
            //context.evaluateString(scope, scriptToRun, "Packages.projections.ScriptingLayer.androidScript", 1, null);

            Function functionAdd = (Function)scope.get("proj",scope);
            Object result = functionAdd.call(
            context, scope, scope, new Object[] {"testProj"});

            System.out.println("the result is : "+((projection)Context.jsToJava(result,projection.class)).getProjectionName());

        } finally {
            Context.exit();
        }
    }

    //receive the Script from the server with Http request
    private  String getScriptFromServer() {


           String  projectionObj = "var projectionToBuild = java.type(\"projections.projection\");";

            String script = "function build(type,pname,cont) " +
                    "{if (type==\"monitor\")" +
                    "{return  projections.CyclicProjectionAbstract(null,null,null);}}";

            return DataItem;


    }


}
