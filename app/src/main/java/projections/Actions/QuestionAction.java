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

    private compositeAction successAcc;
    private compositeAction failAcc;
    boolean isInit;


    public QuestionAction(String txt, String concept, Context _context) {
        super(ActionType.Question, txt, concept, _context);
        _actor = Actor.Patient;
        successAcc = new compositeAction(_context, Utils.ExecuteMode.Sequential);
        failAcc = new compositeAction(_context, Utils.ExecuteMode.Sequential);


    }

    public void addToSuccessAction(Action action) {
        successAcc.addAction(action);
    }


    public void addToFailAction(Action action) {
        failAcc.addAction(action);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String concept = intent.getStringExtra("concept");
        String val = String.valueOf(intent.getStringExtra("value"));
        Log.i("QuestionAction geting value ", "get value of : " + val);
        String time = String.valueOf(intent.getStringExtra("time"));

        // waiting for the answer of the question (yes/no) value
        if (val.equals("yes"))
            successAcc.invoke(false);
        else
            failAcc.invoke(false);
    }


    @Override
    public Message call() {
        Log.i("start building question", "start building question");
        if (!isInit) {
            IntentFilter intentFilter = new IntentFilter(getConcept());
            isInit = true;
            context.registerReceiver(this, intentFilter);
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
