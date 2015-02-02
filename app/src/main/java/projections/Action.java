package projections;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

public  class Action extends BroadcastReceiver {

    protected  String actionName;
    protected  String actionConcept;
    protected String ansVal;
    private final static int  MEASURE_SEND=1;
    private final static int  MEASURE_RECEIVE=2;
    protected ActionType type;
    protected String msgToSend;
    protected Context context;
    protected  int count;
    protected Dictionary<String,DataItem> data;
   // protected final ProjectionBroadCastReciever receiver1 = new ProjectionBroadCastReciever();

    @Override
    public void onReceive(Context context, Intent intent) {

        String concept=intent.getStringExtra("concept");
        String val=intent.getStringExtra("value");

        DataItem item;
        Date dateNow;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
        String now = sdf.format(new Date());
        Intent i=new Intent("trigger");
        context.sendBroadcast(i,android.Manifest.permission.VIBRATE);
        try {
             dateNow=sdf.parse(now);
            item=new DataItem(concept,val,dateNow);
            data.put(concept,item);



        } catch (ParseException e) {
            Log.e("Action","error parsing date in onReceive");
        }

        Toast.makeText(context, "INTENT coount: "+now, Toast.LENGTH_LONG).show();


    }

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
        data=new Hashtable<String,DataItem>() ;


    }
    public Action(ActionType  _type,String name, String concept,Context _context)
    {
        count=0;
        actionName=name;
        type=_type;
        actionConcept=concept;
        ansVal="";
        context=new ContextWrapper(_context);
        data=new Hashtable<String,DataItem>() ;
    }

    public void SubscribeConcept(String concept)
    {

        IntentFilter intentFilter = new IntentFilter(concept);


        context.registerReceiver(this, intentFilter);



    }
    public void UnSubscribeConcept(String concept)
    {
        context.unregisterReceiver(this);

    }
    //send the action to the Mobile Gui
    public Message getActionToSend()
    {

        msgToSend=actionName;
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
