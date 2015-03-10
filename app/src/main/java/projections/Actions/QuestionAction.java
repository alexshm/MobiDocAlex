package projections.Actions;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Moshe on 3/10/2015.
 */
public class QuestionAction extends Action{

    private  compositeAction successAcc;
    private  compositeAction failAcc;


    public QuestionAction(String txt, String concept, Context _context) {
        super(ActionType.Question, txt, concept, _context);
         _actor=Actor.Patient;
        successAcc=null;
        failAcc=null;
        IntentFilter intentFilter = new IntentFilter(concept);

        context.registerReceiver(this, intentFilter);

    }

    public void setSuccessAction(compositeAction action)
    {
        successAcc=action;
    }


    public void setFailAction(compositeAction action)
    {
        failAcc=action;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String concept = intent.getStringExtra("concept");
        String val = String.valueOf(intent.getStringExtra("value"));
        Log.i("QuestionAction geting value ", "get value of : " + val);
        String time = String.valueOf(intent.getStringExtra("time"));

        // waiting for the answer of the question (yes/no) value
        if(val.equals("yes"))
            successAcc.invoke();
        else
            failAcc.invoke();
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
