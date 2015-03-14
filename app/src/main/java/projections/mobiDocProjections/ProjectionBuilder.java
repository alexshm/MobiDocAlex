package projections.mobiDocProjections;


import android.content.Context;
import android.content.ContextWrapper;
import android.util.JsonReader;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import projections.Actions.Action;
import projections.Actions.NotificationAction;
import projections.Actions.QuestionAction;
import projections.Actions.RecommendationAction;
import projections.Actions.compositeAction;
import projections.CyclicProjectionAbstract;
import projections.Actions.MeasurementAction;
import projections.MonitorProjection;
import projections.Utils;
import projections.projection;
import projections.projectionParser.ActionParser;
import projections.projectionParser.parser;
import projections.var;

public class ProjectionBuilder {

    private Context cont;
    private projection projectionToBuild;
    public ProjectionBuilder(Context c)
    {

       // String _jsontest=
        cont=c;
    }

    public projection build(String str)
    {
        parser p=new parser(cont);
        return p.parse(str);
    }
    /*action":[{"type":"mesure","name":"mesurename","conept":"5021"}*/

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




}
