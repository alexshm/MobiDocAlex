package projections.Actions;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.text.ParseException;
import java.util.Date;

import projections.Utils;

/**
 * Action for the  Question operation
 *  in the Call method it return a msg to be sent to the Gui
 */
public class QuestionAction extends Action {

    private String yesConcept;
    private String noConcept;
    public QuestionAction(String txt, String concept) {
        super(ActionType.Question, txt,concept);
        _actor = Actor.Patient;
        noConcept="";
        yesConcept="";
    }

    @Override
    public void setOnReceiveConcept(String onReceiveOp,String concept)
    {
        Log.i("QuestionAction","set on ReceiveConcept for : "+onReceiveOp+" - "+concept);

        if(onReceiveOp.contains("yes"))
            yesConcept=concept;
        else
            noConcept=concept;

    }

    @Override
    public Message call() {

        msgToSend = actionName;
        int msgType = type.ordinal() + 1;

        Message msg = Message.obtain(null, msgType, 0, 0, 0);
        Bundle bundle = new Bundle();

        bundle.putString("value", msgToSend);
        bundle.putString("yes",yesConcept);
        bundle.putString("no", noConcept);
        Log.i("Questoion(build msg)","build msg for : "+actionName +"(yes:"+yesConcept+" , no: "+noConcept );
        msg.setData(bundle);
        return msg;
    }


}
