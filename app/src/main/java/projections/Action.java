package projections;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public  class Action {

    protected  String actionName;
    protected  String actionConcept;
    protected String ansVal;
    private final static int  MEASURE_SEND=1;
    private final static int  MEASURE_RECEIVE=2;
    protected ActionType type;
    protected String msgToSend;
    protected Context context;
    protected final ProjectionBroadCastReciever receiver1 = new ProjectionBroadCastReciever();
    public enum ActionType {
         Question, Recommendation, Notification ,Measurement

    }

    public Action(ActionType _type, String s)
    {
        type =_type;
        actionName="";
        actionConcept="0";
        ansVal="";
        context=new ContextWrapper(null);

    }
    public Action(ActionType  type,String name, String concept,Context _context)
    {
        actionName=name;
        actionConcept=concept;
        ansVal="";
        context=new ContextWrapper(_context);
    }

    public void SubscribeConcept(String concept)
    {

        IntentFilter intentFilter = new IntentFilter(concept);


        context.registerReceiver(receiver1, intentFilter);



    }
    public void UnSubscribeConcept(String concept)
    {
        context.unregisterReceiver(receiver1);

    }
    //send the action to the Mobile Gui
    public Message getActionToSend()
    {

        msgToSend="test msg";
        Message msg = Message.obtain(null,(type.ordinal()+1),0,0,0);
        Bundle bundle = new Bundle();
        bundle.putString("value", msgToSend);
        msg.setData(bundle);
        System.out.println("the data when build is : "+msg.getData().getString("value")+" "+(type.ordinal()+1));
        return  msg;


    }




    static class ActionHandler extends Handler{

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case (MEASURE_RECEIVE):

                    String ans = msg.getData().getString("value");
                    break;
                default:
                    super.handleMessage(msg);
            }


        }

    }

}
