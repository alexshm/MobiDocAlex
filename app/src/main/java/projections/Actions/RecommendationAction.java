package projections.Actions;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import projections.Utils;

/**
 * Action for the  Question operation
 *  in the Call method it return a msg to be sent to the Gui
 */

public class RecommendationAction extends Action{



    private String acceptConcept;
    private String declineConcept;
    boolean isInit;

    public RecommendationAction(String recommendationTxt, String concept ,String accept, String decline,Actor actor,Context c) {
        super(Action.ActionType.Recommendation, recommendationTxt, concept, c);
        _actor=actor;
        _actor=Actor.Patient;

        isInit=false;
    }


    @Override
    public Message call()  {
        Log.i("start building question","start building question");
        if(!isInit)
        {
         isInit=true;
        }

        msgToSend = actionName;
        int msgType = type.ordinal() + 1;

        Message msg = Message.obtain(null, msgType, 0, 0, 0);
        Bundle bundle = new Bundle();

        bundle.putString("value", msgToSend);
        bundle.putString("concept", this.getConcept());
        bundle.putString("accept", acceptConcept);
        bundle.putString("decline", declineConcept);
        Log.i("recommendation action- build msg", "build msg for recommendation ");

        msg.setData(bundle);
        return msg;
    }


}
