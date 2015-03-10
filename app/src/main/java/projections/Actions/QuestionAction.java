package projections.Actions;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

/**
 * Created by Moshe on 3/10/2015.
 */
public class QuestionAction extends Action{
    Actor _actor;

    public QuestionAction(String txt, String concept, Context _context) {
        super(ActionType.Question, txt, concept, _context);
         _actor=Actor.Patient;
    }

    @Override
    public void doAction() {

    }

    @Override
    public Message call()  {
        msgToSend = actionName;
        int msgType = type.ordinal() + 1;

        Message msg = Message.obtain(null, msgType, 0, 0, 0);
        Bundle bundle = new Bundle();

        bundle.putString("value", msgToSend);
        Log.i("question action- buil msg", "build msg for : " + actionName);
        msg.setData(bundle);
        return msg;
    }
}
