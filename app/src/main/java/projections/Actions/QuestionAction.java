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

    public QuestionAction(String txt, String concept) {
        super(ActionType.Question, txt,concept);
        _actor = Actor.Patient;
    }




    @Override
    public Message call() {
        Log.i("start building question", "start building question");
        msgToSend = actionName;
        int msgType = type.ordinal() + 1;

        Message msg = Message.obtain(null, msgType, 0, 0, 0);
        Bundle bundle = new Bundle();

        bundle.putString("value", msgToSend);
        bundle.putString("yesVal", "yes");//TODO: ADD FOR YES AND NO THE YES+NO CONCEPTS
        bundle.putString("noVal", "no");
        Log.i("Questoion(build msg)","build msg for : "+actionName);
        msg.setData(bundle);
        return msg;
    }


}
