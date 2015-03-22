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
    private parser p;
    public ProjectionBuilder(Context c)
    {

       // String _jsontest=
        cont=c;
        p=new parser(cont);

    }

        public void clearParams()
        {
            p.clerParams();
        }
    public void setProjectionParamsTest(String starttime,String remainderAmount,String remainderUnit,String freqAmount,String frequnit) {
       p.setProjectionParamsTest(starttime,remainderAmount,remainderUnit,freqAmount,frequnit);
    }
    public projection build(String str)
    {

        return p.parse(str);
    }
}
