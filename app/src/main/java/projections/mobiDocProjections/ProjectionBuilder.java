package projections.mobiDocProjections;


import android.content.Context;
import android.content.ContextWrapper;
import android.util.JsonReader;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import projections.Actions.Action;
import projections.Actions.NotificationAction;
import projections.Actions.compositeAction;
import projections.CyclicProjectionAbstract;
import projections.Actions.MeasurementAction;
import projections.MonitorProjection;
import projections.Utils;
import projections.projection;

public class ProjectionBuilder {

    private Context cont;
    private projection projectionToBuild;
    public ProjectionBuilder(Context c)
    {

       // String _jsontest=
        cont=new ContextWrapper(c);
    }

    /*action":[{"type":"mesure","name":"mesurename","conept":"5021"}*/

    private Action buildAction(JSONObject actionJson) throws JSONException {

        String actionType =actionJson.getString("type");
        String name =actionJson.getString("name");
        String concept =actionJson.getString("concept");

        Utils.ActionType acType=Utils.getActionType(actionType);
        switch(acType) {
            case Measurement:
                return new MeasurementAction(name, concept, cont);

            case Notification:
                return new NotificationAction(name, concept, null, cont);
        }
        return null;

    }


    private void addActionsToProj(JSONObject actionArrJson) throws JSONException

    {
        String execMode =actionArrJson.getString("mode");
        Utils.ExecuteMode mode=Utils.convertToExecuteMode(execMode);

        projectionToBuild.setExectuionMode(mode);

        JSONArray jArr = actionArrJson.getJSONArray("actions");
        for (int i=0; i < jArr.length(); i++) {
            JSONObject action = jArr.getJSONObject(i);
            Action a=buildAction(action);
            if(a!=null)
                projectionToBuild.addAction(a);
        }
    }

    private void setprojectionFreq(JSONObject freqJson) throws JSONException

    {
        int len=freqJson.length();
        //parse if there is a frequency(for cyc) for this projection
        if(len>0) {

            String startTime = freqJson.getString("start");

            //=========================================
            JSONObject repeatObj = freqJson.getJSONObject("repeat");
            int amount = repeatObj.getInt("amount");
            String unit=repeatObj.getString("unit");
            projection.ProjectionTimeUnit timeUnit=Utils.getTimeUnit(unit);
            //=========================================
            JSONObject reminderObj = freqJson.getJSONObject("reminder");
            int remAmount = reminderObj.getInt("amount");
            String remUnit=reminderObj.getString("unit");
            projection.ProjectionTimeUnit remTimeUnit=Utils.getTimeUnit(unit);


            ((CyclicProjectionAbstract)projectionToBuild).setFrequency(timeUnit,amount);
            ((CyclicProjectionAbstract)projectionToBuild).setReaminder(remTimeUnit,remAmount);
            ((CyclicProjectionAbstract)projectionToBuild).setStartTime(startTime);
        }
    }

    private void addConditionAction(JSONObject actionArrJson) throws JSONException

    {
        int len=actionArrJson.length();
        //parse if there is a condition for this projection
        if(len>0) {

            String execMode = actionArrJson.getString("op");
            Utils.ExecuteMode mode = Utils.convertToExecuteMode(execMode);

            projectionToBuild.setExectuionMode(mode);

            JSONArray jArr = actionArrJson.getJSONArray("actions");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject action = jArr.getJSONObject(i);
                Action a = buildAction(action);
                if (a != null)
                    projectionToBuild.addAction(a);
            }
        }
    }


    private void generateProjection( String type,String name,String id)
    {
        Utils.ProjectionType projectionType=Utils.convertToProjectionType(type);

        switch(projectionType) {
            case Cyclic:
               projectionToBuild= new CyclicProjectionAbstract(name,cont,"");
               break;
            case Monitor:
                projectionToBuild=new MonitorProjection(name,cont);
                break;
            default:
                projectionToBuild= null;
        }

    }
    public projection FromJson(String jsonstr)
    {

        try {
            if (jsonstr!=null) {
                JSONObject jObj = new JSONObject(jsonstr);
                JSONObject projectionjson = jObj.getJSONObject("projection");
                String projectionType = projectionjson.getString("type");
                String projectionName = projectionjson.getString("name");
                String id = projectionjson.getString("id");

                generateProjection(projectionType, projectionName, id);

                JSONObject actionJson = projectionjson.getJSONObject("action");
                addActionsToProj(actionJson);

                JSONObject conditionActionJson = projectionjson.getJSONObject("condition");
                addConditionAction(conditionActionJson);

                JSONObject freqJson = projectionjson.getJSONObject("freq");
                setprojectionFreq(freqJson);
            }

            return projectionToBuild;

        } catch (JSONException e) {
            Log.e("projection builder","error building projection: "+e.getMessage());
            return  null;
        }

    }

    public projection SimulateBuild_Projection(String keyID)

    {
        projection cyc=null;
        switch (keyID) {
            case "19964":
                cyc = new CyclicProjectionAbstract(keyID, cont, "") {

                    @Override
                    public void doAction() {
                        System.out.println("this is a cyclic proj Ketonuria");
                    }

                    @Override
                    public void makeTestCyclic() {
                        compositeAction ac=new compositeAction(cont, Utils.ExecuteMode.Parallel);

                        MeasurementAction m1 = new MeasurementAction("mesure Ketonuria", "5021", cont);
                        ac.addAction(m1);


                       // setAction(m1);

                        //setFrequency(Minute,2);
                        //setReaminder(Second,30);
                    }
                };
                break;
            case "20093":
                cyc = new CyclicProjectionAbstract(keyID, cont, "") {
                    @Override
                    public void doAction() {
                        System.out.println("this is a cyclic proj Routine Daily BG - launch");
                    }

                    @Override
                    public void makeTestCyclic() {
                        MeasurementAction m1 = new MeasurementAction("BG Lunch", "4987", cont);
                        //setAction(m1);

                        //setFrequency(Minute,1);
                        // setReaminder(Second,40);
                    }


                };
                break;
            case "20092":
                cyc = new CyclicProjectionAbstract(keyID, cont, "") {
                    @Override
                    public void doAction() {
                        System.out.println("this is a cyclic proj Routine Daily BG-Breakfast");
                    }

                    @Override
                    public void makeTestCyclic() {
                        MeasurementAction m1 = new MeasurementAction("BG Breakfast", "4986", cont);
                       // this.setAction(m1);

                        //setFrequency(Second,30);
                        //setReaminder(Second,40);
                    }
                };
                break;

            case "20091":
                cyc = new CyclicProjectionAbstract(keyID, cont, "") {
                    @Override
                    public void doAction() {
                        System.out.println("this is a call back test");
                    }

                    @Override
                    public void makeTestCyclic() {
                        MeasurementAction m1 = new MeasurementAction("BG Fasting", "4985", cont);
                       // this.setAction(m1);

                        //setFrequency(Second,50);
                        //setReaminder(Second,5);
                    }
                };
                break;
            case "20026":
                cyc = new CyclicProjectionAbstract(keyID, cont, "") {
                    @Override
                    public void makeTestCyclic() {
                        MeasurementAction m1 = new MeasurementAction("BG Fasting", "4985", cont);
                        //this.setAction(m1);
                    }
                };
                break;

        }
        return  cyc;
    }

    public projection BuildProjection()
    {
        return  null;

    }
}
