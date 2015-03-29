package projections.ScriptingLayer;

import android.util.Log;

import com.google.dexmaker.TypeId;

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
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Moshe on 3/18/2015.
 */

 interface cont
{
    
}

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


    public void continueTest()
    {

        /*
            we will create the obj projection not in script
            for the recommendation/qeustion we will make
            set concepts for answers(or set answerConcepts)
            then we will setOnconceptRecive(string concept,composite action)

            the declaration of the composite action will done erallier and we pass just
            the name(or id) and search in the map /globals vars

            then we will do something like p.action.setOncecept..()
            and inside the method that will implemented in the projection class
            we search the name in the composite actions map and connect to it.

          1)  in the java scriipt i declare an object - actionSequance()-emotye constractor
            actionsequance: name
                           : list of actionsToPreform need to be also actionsToPreform() -constractor
                                      each action like this has:
                                      {
                                            name(identifier)
                                             concept
                                             type(measure/callback/quetion/notifiaction....)
                                             txt of msg

                                          }
                           :order (sequncial/parralel)
           2)  we will have javascript function -declareActionsequance(name)
                in this function we call to a function in ht projection class that init a new CompositeAction
                and insert to the map of composite.
            3) we will set a function to the javascript actionSequance obj that will add a  new
            actionsToPreform() obj will all the needed params.
            4) than again in the javascript function -declareActionsequance(name):-
                we will iterate for all the actions in the list and build a coresponding Action object
                andd add to the composite action we declare before.



               5) need to change the monitoring/cyclic projection for triggring the action by name

           --    6) add implementation to thr projection addAction(name..params)/(name,Action)




         */

    }

    public projection buildProjection()
    {
        return null;
    }
    public   void runScript(String scriptToRun,projection p) {

       scriptToRun=getScriptFromServer();
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
            Class<DataItem> dataitemClass = (Class<DataItem>)classloader.loadClass("projections.DataItem");
            scope.put("dataitemClass",scope,dataitemClass);
            scope.put("proj",scope,p);

            scope.put("builder", scope, new ProjectionBuilder(c));


            // Build the script
            String script ="var today = new java.util.Date();java.lang.System.out.println('Today is ' + today);" +

                 //   "var item=test.newInstance();" +
                //    "var ans=item.testing();" +
                 //   "java.lang.System.out.println('the ans is  ' + ans);"+
                 //   "var temp= builder.buildNewProjecction('cyc','11','44');"+
                //   "java.lang.System.out.println('the type is   ' + temp.getProjectionName());"+


                    "var newinstance=new actionsToPreform('11' ,'22','22','test');" +

                    "var comp=new actionSequance();"+
                   //"java.lang.System.out.println('the size is : '+comp.actionList.length);"+
                    //"comp.actionList.push(newinstance);"+

                    "comp.addAction(newinstance);"+
                     "java.lang.System.out.println('the size is : '+comp.actionList.length);"+
                    "comp.printElements();";



         // Execute the script

            Object obj = context.evaluateString( scope, scriptToRun+script, "TestScript", 1, null );
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
    private  String getScriptFromServer() {



            String script =
                    "function actionsToPreform(name,type,concept,txt)"
                    +"{this.name=name;"+
                      "this.type=type;this.concept=concept;this.txt=txt;};"+

                    "function actionSequance(name) {" +
                            "this.name=name;};"+


                    " actionSequance.prototype.actionList=[];"+
                    "actionSequance.prototype.addAction=function(action){\n" +
                            "actionSequance.prototype.__defineSetter__('addaction', function(action)\n\t" +
                                     "{ actionSequance.prototype.actionList.push(action);});\n" +
                                      "this.addaction=action;\n"+
                                      // "java.lang.System.out.println('the size is : '+actionList);" +
                            "};"+

                    " actionSequance.prototype.printElements=function(){" +
                            "for(var i=0;i< actionSequance.prototype.actionList.length;i++)\n" +
                            "{\n" +
                            "java.lang.System.out.println('the actionName is :  ' + actionSequance.prototype.actionList[i].name);" +
                            "}"+
                            "};" ;


                            /*
                       "this.printElements=function(){" +
                            "for(var i=0;i< this.actionList.length;i++)\n" +
                            "{\n" +
                            "java.lang.System.out.println('the actionName is :  ' + actionList[i].name);" +
                            "}"+
                           "};" +
                           "};\n\n" ;
                           */





            return script;


    }


}
