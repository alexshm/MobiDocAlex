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
    public RecommendationAction(String recommendationTxt, String concept ,Actor actor) {
        super(Action.ActionType.Recommendation, recommendationTxt, concept);
        _actor=actor;
        _actor=Actor.Patient;
        declineConcept="";
        acceptConcept="";
    }

    @Override
    public void setOnReceiveConcept(String onReceiveOp,String concept)
    {
        Log.i("RecommendationAction","set on ReceiveConcept for : "+onReceiveOp+" - "+concept);
        if(onReceiveOp.contains("accept"))
            acceptConcept=concept;
        else
            declineConcept=concept;

    }
    @Override
    public Message call()  {

        msgToSend = actionName;
        int msgType = type.ordinal() + 1;
        Message msg = Message.obtain(null, msgType, 0, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString("value", msgToSend);
        bundle.putString("concept", this.getConcept());
        bundle.putString("accept", acceptConcept);
        bundle.putString("decline", declineConcept);
        Log.i("recommendation action", "build msg for recommendation : "+actionName+ " (accept: "+acceptConcept+" , decline: "+declineConcept );

        msg.setData(bundle);
        return msg;
    }


}
