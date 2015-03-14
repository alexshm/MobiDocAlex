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
}
