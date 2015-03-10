package projections.Actions;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class RecommendationAction extends Action{


    private  compositeAction successAcc;
    private  compositeAction failAcc;


    public RecommendationAction(String recommendationTxt, String concept, Actor actor,Context c) {
        super(Action.ActionType.Recommendation, recommendationTxt, concept, c);
        _actor=actor;
        _actor=Actor.Patient;
        successAcc=null;
        failAcc=null;
        IntentFilter intentFilter = new IntentFilter(concept);

        context.registerReceiver(this, intentFilter);
    }


    @Override
    public void doAction() {

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
        Log.i("RecommendationAction geting value ", "get value of : " + val);
        String time = String.valueOf(intent.getStringExtra("time"));

        // waiting for the answer of the question (yes/no) value
        if(val.equals("yes"))
            successAcc.invoke();
        else
            failAcc.invoke();
    }

    @Override
    public Message call()  {
        msgToSend = actionName;
        int msgType = type.ordinal() + 1;

        Message msg = Message.obtain(null, msgType, 0, 0, 0);
        Bundle bundle = new Bundle();

        bundle.putString("value", msgToSend);
        Log.i("recommendation action- buil msg", "build msg for : " + actionName);
        msg.setData(bundle);
        return msg;
    }
}
