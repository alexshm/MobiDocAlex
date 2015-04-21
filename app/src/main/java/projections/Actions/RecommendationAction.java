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

    boolean isInit;

    public RecommendationAction(String recommendationTxt, String concept ,Actor actor) {
        super(Action.ActionType.Recommendation, recommendationTxt, concept);
        _actor=actor;
        _actor=Actor.Patient;
        isInit=false;
    }


    @Override
    public Message call()  {

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
        bundle.putString("accept", "");//TODO: add the decline +accept
        bundle.putString("decline", "");
        Log.i("recommendation action", "build msg for recommendation ");

        msg.setData(bundle);
        return msg;
    }


}
