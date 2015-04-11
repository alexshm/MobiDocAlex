package projections.ScriptingLayer;

import android.content.res.AssetManager;
import android.util.Log;

import com.google.dexmaker.TypeId;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.*;

import dalvik.system.DexClassLoader;
import projections.DataItem;
import projections.MonitorProjection;
import projections.Utils;
import projections.mobiDocProjections.ProjectionBuilder;
import projections.projection;
import projections.CyclicProjectionAbstract;
import projections.projection.ProjectionTimeUnit;
import org.mozilla.classfile.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    private String basicScript;
    private String s;
        android.content.Context c;
    public JsScriptExecutor( android.content.Context cont)
    {
        c=cont;
        basicScript=readbasicScript("projectionscript");

    }


    private projection buildProj(String script)
    {
        String temp = script;
        final Pattern pattern = Pattern.compile(".*?beginProjection\\((.*?,.*?,*,?)\\);*?");
        final Matcher m = pattern.matcher("");
        //eliminate spaces and new lines
        temp = temp.replaceAll("\\r\\n|\\r|\\n|\\t", "").trim();
        String replacedScript=temp;
        m.reset(temp);
        if (m.find()) {
            String[] projection = m.group(1).substring(1,m.group(1).length()-1).split("','");
            String type = projection[0];
            String name = projection[1];
            String id = projection[2];

            projection.ProjectionType projectionType = Utils.convertToProjectionType(type);

            switch (projectionType) {
                case Cyclic:
                    return new CyclicProjectionAbstract(name, id, c);

                case Monitor:
                    return new MonitorProjection(name, id, c);

            }
        }

        return null;

    }


    private String preProssesing(String script) {
        String temp = script;
        final Pattern pattern = Pattern.compile(".*?var\\s([a-z|A-Z|0-9|_]+=declareActionsequance\\('seq'\\)\\{(.*?))\\};.*?");
        final Pattern varsPattern = Pattern.compile(".*?setVar\\('([a-z|A-Z|0-9|_]+','(.*?)')\\);.*?");
        final Pattern startprojPattern = Pattern.compile(".*?start\\((.*?)\\);");
       final Pattern monitoringPattern = Pattern.compile(".*?performMonitoringOn\\s(.*?).*?forTime.*?;");

         Matcher m = pattern.matcher("");
        //eliminate spaces and new lines
        String beginstr=temp.split(":")[0];
        temp=temp.replace(beginstr+":","");
        temp = temp.replaceAll("\\r\\n|\\r|\\n|\\t", "").trim();
        String replacedScript=temp;
        m.reset(temp);
        //
        while (m.find()) {

            String declareActionScript = m.group(1);
            String innerDeclareActionScript = m.group(2);
            String compositename = declareActionScript.split("=")[0];
            String compType=declareActionScript.split("'")[1];
            String newScript= evalScipt(innerDeclareActionScript, compositename, compType);
            replacedScript = replacedScript.replace(declareActionScript+"};", newScript).trim();
        }

        //========================================================================
        //========================================================================

        m = varsPattern.matcher("");
        m.reset(replacedScript);
        // search for conditions vars declarations
        while (m.find()) {
            String setVarScript = m.group(0);
            String varDeclarationScript = m.group(1);
            String[] parms = m.group(2).split("','");
            String varName = varDeclarationScript.split("'")[0];
            String varType=parms[1];
            String varConcept=parms[0];
            String newScript=  "var "+varName+"=new conditionVar('"+varName+"','"+varType+"','"+varConcept+"');";
            replacedScript = replacedScript.replace(setVarScript, newScript).trim();


        }

        //========================================================================
        //========================================================================
        String monitoringCondition="";
        m = monitoringPattern.matcher("");
        m.reset(replacedScript);
        // search for conditions vars declarations
        while (m.find()) {
            String monitoringScript = m.group(0);
            monitoringCondition="var monitoringAns=monitoringPreProcesing('"+monitoringScript+"');";
               // "print('the monitor condition is : '+monitoringAns[0]+' '+monitoringAns[1]+' ' +monitoringAns[2]+ ' '"+
                 //       "monitoringAns[3]+' '+monitoringAns[4]+' ' +monitoringAns[5]);";
            replacedScript = replacedScript.replace(monitoringScript,monitoringCondition ).trim();


        }
        ///======================

        m = startprojPattern.matcher("");
        m.reset(replacedScript);
        // search for conditions vars declarations
        if (m.find()) {
            String startCommand = m.group(0);

            replacedScript = replacedScript.replace(startCommand, finishScript()+"\n"+startCommand+monitoringCondition+"\neval(monitoringAns);monitoringAns;").trim();
        }
        else //not find the start command( usually when the projection is MONITOR type
        {
            replacedScript += finishScript()+"\n"+monitoringCondition+"\neval(monitoringAns);monitoringAns;";

        }
        return replacedScript;
    }


    private String evalScipt(String script,String compositeActionName,String type)

    {
        final Pattern pattern = Pattern.compile(".*?([a-z|A-Z|0-9|_]+)=new\\sAction\\(.*?\\);");
        final Matcher m = pattern.matcher("");
        m.reset(script);
        String declareCompositeAction = compositeActionName+"=new actionSequance('"+compositeActionName+"');\n"+
                compositeActionName+".order='"+type+"';";

        String addScript="";
        while (m.find()) {

            String action = m.group(1);
            addScript += compositeActionName + ".addAction(" + action + ");\n";
        }
            return declareCompositeAction+script+addScript+"\n";
    }



    public   projection runScript(String scriptToRun) {


        projection projToBuild=buildProj(scriptToRun);
        Log.i("JSSCRIPTING-runSctipt","initiazing projectoin ...with type : "+projToBuild.getType().name()+" with id : "+projToBuild.getProjectionId());
        String script=preProssesing(scriptToRun);
        Log.i("JSSCRIPTING","the new script after preProssesing is :"+script);

        Context context = Context.enter();
        context.setOptimizationLevel(-1);

        try {
            // Initialize the standard objects (Object, Function, etc.). This must be done before scripts can be
            // executed. The null parameter tells initStandardObjects
            // to create and return a scope object that we use
            // in later calls.

            Scriptable scope = context.initStandardObjects();
            scope.put("proj", scope, projToBuild);
            // Build the script


            // Execute the script
            String evalScript=basicScript+" \n "+script;
          //  System.out.println(evalScript);
            Object obj =context.evaluateString(scope,evalScript, "TestScript", 1, null);
            Log.i("JsScripting- runscript" ,"finishing evaluating and executing script");
            //System.out.println("the result is : "+((projection)Context.jsToJava(obj,projection.class)).getProjectionName());

         //   projToBuild=(projection)obj;
            // Cast the result to a string


        } catch (Exception e) {
            Log.e("JsScripting- runscript" ,"error evaluating and executing script. error msg : "+e.getMessage());



        } finally {
            // Exit the Context. This removes the association between the Context and the current thread and is an
            // essential cleanup action. There should be a call to exit for every call to enter.
            Context.exit();
            return projToBuild;
        }


    }
    private  String finishScript() {
        String finishScript =
                "insertActionToProjection();\n"+
                "insertVarsToProjection();";
                return finishScript;

    }




    private String readbasicScript(String FileName) {
        try {

                //=================================
                // read file   data from raw resources
                //==========================

                InputStream iS;

                int rID = c.getResources().getIdentifier("example.com.mobidoc:raw/" + FileName, null, null);
                iS = c.getResources().openRawResource(rID);

                //create a buffer that has the same size as the InputStream
                byte[] buffer = new byte[iS.available()];
                //read the text file as a stream, into the buffer
                iS.read(buffer);
                //create a output stream to write the buffer into
                ByteArrayOutputStream oS = new ByteArrayOutputStream();
                //write this buffer to the output stream
                oS.write(buffer);
                //Close the Input and Output streams
                oS.close();
                iS.close();

                //return the output stream as a String
                return oS.toString();

            } catch (IOException e) {
                Log.e("read Projecction file in", "error reading file: " + e.getMessage());
                return null;
            }
        }





}
