package projections.Actions;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import projections.Utils;

public class RecommendationAction extends Action{


    private  compositeAction successAcc;
    private  compositeAction failAcc;
    private String acceptConcept;
    private String declineConcept;
    boolean isInit;

    public RecommendationAction(String recommendationTxt, String concept ,String accept, String decline,Actor actor,Context c) {
        super(Action.ActionType.Recommendation, recommendationTxt, concept, c);
        _actor=actor;
        _actor=Actor.Patient;
        successAcc=new compositeAction(c, Utils.ExecuteMode.Sequential);
        failAcc=new compositeAction(c, Utils.ExecuteMode.Sequential);
        acceptConcept=accept;
        declineConcept=decline;
        isInit=false;
    }


    @Override
    public void doAction() {

    }


    public void addToSuccessAction(Action action)
    {
        successAcc.addAction(action);
    }


    public void addToFailAction(Action action)
    {
        failAcc.addAction(action);
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
        Log.i("start building question","start building question");
        if(!isInit)
        {
            IntentFilter acceptFilter = new IntentFilter(acceptConcept);
            isInit=true;
            context.registerReceiver(this, acceptFilter);
            IntentFilter declineFilter = new IntentFilter(declineConcept);
            context.registerReceiver(this, declineFilter);
        }

        msgToSend = actionName;
        int msgType = type.ordinal() + 1;

        Message msg = Message.obtain(null, msgType, 0, 0, 0);
        Bundle bundle = new Bundle();

        bundle.putString("value", msgToSend);

        bundle.putString("accept", acceptConcept);
        bundle.putString("decline", declineConcept);
        Log.i("recommendation action- build msg", "build msg for recommendation ");

        msg.setData(bundle);
        return msg;
    }
}
