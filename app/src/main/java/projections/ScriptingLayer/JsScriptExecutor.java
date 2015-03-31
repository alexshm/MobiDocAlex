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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String preProssesing(String script) {
        String temp = script;//"(waitPeriodic)(.\".*;\"*,)(.*?,)(.*?)(;)"
        final Pattern pattern = Pattern.compile("(.*?)(declareActionsequance\\{var.*?\\};.*?)");
        final Matcher m = pattern.matcher("");

        //eliminate spaces and new lines
        temp = temp.replaceAll("\\r\\n|\\r|\\n|\\t", "").trim();

        m.reset(temp);
        while (m.find()) {
            String comnd1 = m.group(0);
            String comnd2 = m.group(1);
            String comnd = m.group(2);

            int y=0;
        }

        return null;
    }


    private void evalScipt(String script)

    {


    }

    public void continueTest()
    {

        /*
     --done--   1) we will create the obj projection not in script
    --done--    2) for the recommendation/qeustion we will make
    --done-     3)  set concepts for answers(or set answerConcepts)
    --done--   4) then we will setOnconceptRecive(string concept,composite action)

           5) the declaration of the composite action will done erallier and we pass just
            the name(or id) and search in the map /globals vars

    --done--      6)   then we will do something like p.action.setOncecept..()
    --done--   7)  and inside the method that will implemented in the projection class
                we search the name in the composite actions map and connect to it.

    --done--    1)  in the java scriipt i declare an object - actionSequance()-emotye constractor
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
    --done--     2)  we will have javascript function -declareActionsequance(name)
                in this function we call to a function in ht projection class that init a new CompositeAction
                and insert to the map of composite.
    --done--    3) we will set a function to the javascript actionSequance obj that will add a  new
            actionsToPreform() obj will all the needed params.
    --done--    4) than again in the javascript function -declareActionsequance(name):-
                we will iterate for all the actions in the list and build a coresponding Action object
                andd add to the composite action we declare before.

    --done--   6) add implementation to thr projection addAction(name..params)/(name,Action)

         */

    }

    public projection buildProjection()
    {
        return null;
    }
    public   void runScript(String scriptToRun,projection p) {

        String initScript = initScript();
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        try {
            // Initialize the standard objects (Object, Function, etc.). This must be done before scripts can be
            // executed. The null parameter tells initStandardObjects
            // to create and return a scope object that we use
            // in later calls.
            Scriptable scope = context.initStandardObjects();
            scope.put("classLoader", scope, c.getClass().getClassLoader());
            final String libPath = "";
            final File tmpDir = c.getDir("dex", 0);
            final DexClassLoader classloader = new DexClassLoader(libPath, tmpDir.getAbsolutePath(), null, c.getClassLoader());
            scope.put("DexClassLoader", scope, classloader);

            Class<DataItem> dataitemClass = (Class<DataItem>) classloader.loadClass("projections.DataItem");
            scope.put("dataitemClass", scope, dataitemClass);
            scope.put("proj", scope, p);

            scope.put("builder", scope, new ProjectionBuilder(c));


            // Build the script
            String script = "var today = new java.util.Date();java.lang.System.out.println('Today is ' + today);" +

                    //   "var item=test.newInstance();" +
                    //    "var ans=item.testing();" +
                    //   "java.lang.System.out.println('the ans is  ' + ans);"+
                    //   "var temp= builder.buildNewProjecction('cyc','11','44');"+
                    //   "java.lang.System.out.println('the type is   ' + temp.getProjectionName());"+


                    "var newinstance=new actionsToPreform('katenuria' ,'22','22','test');" +

                    "var comp=new actionSequance('compkatenuria','seq');" +
                    "java.lang.System.out.println('the size is : '+compositeCollection.length);" +
                    //"java.lang.System.out.println('the size is : '+comp.actionList.length);"+
                    //"comp.actionList.push(newinstance);"+
                    "if(compositeCollection.length>0)" +
                    // "java.lang.System.out.println('the size is : '+compositeCollection[0].name);"+
                    "comp.addAction(newinstance);" +
                    "java.lang.System.out.println('the size is : '+compositeCollection[0].actionList.length);" +
                    "comp.printElements();" +
                    "var comp1=new actionSequance('testtt','seq');" +
                    "var newinstance2=new actionsToPreform('katenuria222' ,'22','22','test');" +
                    "comp1.addAction(newinstance2);" +
                    "java.lang.System.out.println('the action to do before is : '+newinstance.actionToDo.length);"+
                    "newinstance.setOnConceptRecive('12','test');"+
                    "java.lang.System.out.println('the action to do after is : '+newinstance.actionToDo[0].compName);"+
                    "java.lang.System.out.println('the size is : '+compositeCollection.length);";


            // Execute the script
            String evalScript=initScript+" \n "+script+" \n "+finishScript();
          //  System.out.println(evalScript);
            Object obj = context.evaluateString(scope,evalScript, "TestScript", 1, null);
            System.out.println("Object: " + obj);

            // Cast the result to a string


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Exit the Context. This removes the association between the Context and the current thread and is an
            // essential cleanup action. There should be a call to exit for every call to enter.
            Context.exit();
        }

    }
    private  String finishScript() {
        String finishScript ="";
                //"insertActionToProjection();";

                return finishScript;

    }

    //receive the Script from the server with Http request
    private  String initScript() {



            String script =

                    "compositeCollection=[];"+
                    "function actionsToPreform(name,type,concept,txt)"+
                    "{this.name=name;"+
                      "this.type=type;this.concept=concept;this.txt=txt;" +
                           "}"+
                " actionsToPreform.prototype.actionToDo=[];"+
                "actionsToPreform.prototype.setOnConceptRecive=function(c,compActionName){\n" +
                "actionsToPreform.prototype.__defineSetter__('setaction', function(action)\n\t" +
                "{actionsToPreform.prototype.actionToDo.push(action);});\n" +
                "var a={compName:compActionName,concept:c};this.setaction=a;\n"+
                "};\n\n"+

                    "function actionSequance(name,ExecutionOrder) {" +
                            "this.name=name;" +
                            "this.order=ExecutionOrder;" +
                            "compositeCollection.push(this);}"+


                    " actionSequance.prototype.actionList=[];"+
                    "actionSequance.prototype.addAction=function(action){\n" +
                            "actionSequance.prototype.__defineSetter__('addaction', function(action)\n\t" +
                                     "{ actionSequance.prototype.actionList.push(action);});\n" +
                                      "this.addaction=action;\n"+
                            "};"+

                    " actionSequance.prototype.printElements=function(){" +
                            "for(var i=0;i< actionSequance.prototype.actionList.length;i++)\n" +
                            "{\n" +
                            "java.lang.System.out.println('the actionName is :  ' + actionSequance.prototype.actionList[i].name);" +
                            "}"+
                            "};\n\n\n"+

                    "declareActionsequance{var t='test';};"+
                    "function insertActionToProjection()" +
                            "{" +
                            "for(var i=0;i< compositeCollection.length;i++)\n" +
                            "{\n" +
                                "var compositeName=compositeCollection[i].name;"+

                                "for(var j=0;i< compositeCollection[i].length;j++)\n" +
                                 "{proj.addActionToComposite(compositeName,compositeCollection[i].type,compositeCollection[i].txt,"+
                                                            "compositeCollection[i].concept);" +
                                    "var conceptsList=compositeCollection[i].actionToDo;\n" +
                                    "for(var k=0;i< conceptsList.length;j++)"+
                                       "{var conceptToReceive=conceptsList[k].concept;"+
                                        "proj.setOnReceiveConcept(compositeName,conceptToReceive);"+
                                        "}"+
                                "}"+
                            "}};\n\n\n";


        preProssesing(script);
        return script;


    }


}
