package projections.projectionParser;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import projections.Actions.Action;
import projections.Actions.CallBackAction;
import projections.Actions.MeasurementAction;
import projections.Actions.NotificationAction;
import projections.Actions.QuestionAction;
import projections.Actions.RecommendationAction;
import projections.Utils;

/**
 * Created by Moshe on 2/2/2015.
 */
public class ActionParser {

    private  String action;
    private Action.ActionType type;
    private Context context;
     public  ActionParser(Action.ActionType ActionType,Context c) {
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
        return  new MeasurementAction(name,conceptId);
    }


    private Action parseNotification(String[] actionParms)
    {

        String notificationTxt=actionParms[0];
        String conceptId=actionParms[1];
       // TODO: Action.Actor ac= Action.Actor.valueOf(actionParms[2]);
        Action.Actor ac= Action.Actor.Patient;
        return  new NotificationAction(notificationTxt,conceptId,ac);
    }

    private Action parseRecommendation(String[] actionParms)
    {
        String RecommendationTxt=actionParms[0];
        String conceptId=actionParms[1];

        Action.Actor actor= Action.Actor.Patient;
       return new RecommendationAction(RecommendationTxt,conceptId,actor);

    }

    private Action parseQuestion(String[] actionParms)
    {
        String QuesTxt=actionParms[0];
        String conceptId=actionParms[1];

        Action.Actor ac= Action.Actor.Patient;
        return   new QuestionAction(QuesTxt,conceptId);

    }

    private Action parseCallBack(String[] actionParms)
    {
        String name=actionParms[1];
        String conceptId=actionParms[0];
        return  new CallBackAction(name,conceptId);
    }


}

