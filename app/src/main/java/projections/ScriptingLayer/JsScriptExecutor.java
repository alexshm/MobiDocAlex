package projections.ScriptingLayer;

import android.util.Log;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.*;

import dalvik.system.DexClassLoader;
import projections.DataItem;
import projections.mobiDocProjections.ProjectionBuilder;
import projections.projection;
import projections.CyclicProjectionAbstract;
import projections.projection.ProjectionTimeUnit;
import org.mozilla.classfile.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;

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
        /* IMPORTANT - INCLUDE THIS IN THE PARSER
    <script language="javascript">
    load('foo.js');
    </script>
*/
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


      //  scriptToRun=getScriptFromServer();
       Context context = Context.enter();
        context.setOptimizationLevel(-1);
       try
        {
            // Initialize the standard objects (Object, Function, etc.). This must be done before scripts can be
            // executed. The null parameter tells initStandardObjects
            // to create and return a scope object that we use
            // in later calls.
            Scriptable scope = context.initStandardObjects();
            scope.put("classLoader", scope, c.getClass().getClassLoader());
            final String libPath = "";
            final File tmpDir = c.getDir("dex", 0);
            final DexClassLoader classloader = new DexClassLoader(libPath, tmpDir.getAbsolutePath(), null, c.getClassLoader());
            scope.put("DexClassLoader",scope,classloader);
            Class<Object> testclass = (Class<Object>)classloader.loadClass("example.com.mobidoc.test");
            scope.put("test",scope,testclass);
            Class<Object> dataitemClass = (Class<Object>)classloader.loadClass("projections.DataItem");
            scope.put("dataitemClass",scope,dataitemClass);

            scope.put("builder",scope,new ProjectionBuilder(c));
            System.out.println("after finishing loading classes");


            // Build the script
            String script ="var today = new java.util.Date();java.lang.System.out.println('Today is ' + today);" +

                    "var item=test.newInstance();" +
                    "var ans=item.testing();" +
                    "java.lang.System.out.println('the ans is  ' + ans);"+
                    "var temp= builder.buildNewProjecction('cyc','11','44');"+
                   "java.lang.System.out.println('the type is   ' + temp.getProjectionName());";

           //working "var newinstance=dataitemClass(con ,eee,today);";
                   // "var createdata= function (concept,val,date) {var newinstance=dataitemClass.newInstance();";
           // "var methodbuildProj = DataItem.getMethod(\"buildProj\", [java.lang.String]);" +
                 //   "var proj = function (name) {return methodbuildProj.invoke(null, name);};";
                           // "var proj = function (name) {return methodbuildProj.invoke(null, name);};";
                 //   "var s = item('12','111',today);";

            // Execute the script

            Object obj = context.evaluateString( scope, script, "TestScript", 1, null );
            System.out.println( "Object: " + obj );

            // Cast the result to a string


        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            // Exit the Context. This removes the association between the Context and the current thread and is an
            // essential cleanup action. There should be a call to exit for every call to enter.
            Context.exit();
        }



}

    //receive the Script from the server with Http request
    private  String getScriptFromServer(String r) {


           String  projectionObj = "var projectionToBuild = java.type(\"projections.projection\");";

            String script = "function build(type,pname,cont) " +
                    "{if (type==\"monitor\")" +
                    "{return  projections.CyclicProjectionAbstract(null,null,null);}}";

            return DataItem;


    }


}
