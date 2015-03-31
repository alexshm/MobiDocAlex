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


    boolean isInit;


    public QuestionAction(String txt, String concept, Context _context) {
        super(ActionType.Question, txt, concept, _context);
        _actor = Actor.Patient;
    }


    @Override
    public  void setOnReceiveConcept(String compositeActionName, String concept)
    {

    }

    @Override
    public Message call() {
        Log.i("start building question", "start building question");
        if (!isInit) {

            isInit = true;

        }
        msgToSend = actionName;
        int msgType = type.ordinal() + 1;

        Message msg = Message.obtain(null, msgType, 0, 0, 0);
        Bundle bundle = new Bundle();

        bundle.putString("question", msgToSend);
        bundle.putString("yesVal", "yes");
        bundle.putString("noVal", "no");
        Log.i("question action- build msg", "build msg for : ");
        msg.setData(bundle);
        return msg;
    }


}
