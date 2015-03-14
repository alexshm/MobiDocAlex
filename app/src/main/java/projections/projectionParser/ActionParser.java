package projections.projectionParser;

import android.app.Application;
import android.content.Context;

import projections.Actions.Action;
import projections.Actions.CallBackAction;
import projections.Actions.MeasurementAction;
import projections.Utils;

/**
 * Created by Moshe on 2/2/2015.
 */
public class ActionParser {

    private  String action;
    private Utils.ActionType type;
    private Context context;
     public  ActionParser(Utils.ActionType ActionType,Context c) {
        type=ActionType;
         context=c;
     }

    public Action parse(String[] actionParms)
    {
        switch (type) {
            case Measurement:
                return parseMeasure(actionParms);
            case Recommendation:
                return parseRecommendation(actionParms);
            case CallBack:
                return parseCallBack(actionParms);
            case Notification:
                return parseNotification(actionParms);
            case Question:
                return parseQuestion(actionParms);

        }
        return null;

    }
    private Action parseMeasure(String[] actionParms)
    {
       String name=actionParms[0];
        String conceptId=actionParms[1];
        return  new MeasurementAction(name,conceptId, context);
    }


    private Action parseNotification(String[] actionParms)
    {
        //TODO:
        return null;
    }

    private Action parseRecommendation(String[] actionParms)
    {
        //TODO:
        return null;
    }

    private Action parseQuestion(String[] actionParms)
    {
        //TODO:
        return null;
    }

    private Action parseCallBack(String[] actionParms)
    {
        String name=actionParms[1];
        String conceptId=actionParms[0];
        return  new CallBackAction(name,conceptId, context);
    }


}

