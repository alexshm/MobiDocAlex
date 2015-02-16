package projections;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

public  abstract class Action extends BroadcastReceiver {

    protected  String actionName;
    protected  String actionConcept;
    protected String ansVal;
    protected final static int  MEASURE_SEND=1;
    protected final static int  MEASURE_RECEIVE=2;
    protected ActionType type;
    protected String msgToSend;
    protected Context context;
    protected  int count;
    protected Hashtable<String,DataItem> data;


    protected Vector<var> vars;
   // protected final ProjectionBroadCastReciever receiver1 = new ProjectionBroadCastReciever();

    @Override
    public void onReceive(Context context, Intent intent) {

        String concept=intent.getStringExtra("concept");
        String val=intent.getStringExtra("value");

        DataItem item;
        Date dateNow;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
        String now = sdf.format(new Date());

        Intent i=new Intent("trigger2");
        Log.i("trigger from Action","trigger from action "+actionName);
        context.sendBroadcast(i,android.Manifest.permission.VIBRATE);
        try {
             dateNow=sdf.parse(now);
            item=new DataItem(concept,val,dateNow);
            data.put(concept,item);



        } catch (ParseException e) {
            Log.i("Action","error parsing date in onReceive");
        }

        Toast.makeText(context, "INTENT coount: "+now, Toast.LENGTH_LONG).show();


    }

    public enum ActionType {
         Question, Recommendation, Notification ,Measurement,General

    }



    public Action(ActionType _type, String s)
    {
        type =_type;
        actionName="";
        actionConcept="0";
        ansVal="";
        context=new ContextWrapper(null);
        data=new Hashtable<String,DataItem>() ;
        vars=new Vector<var>();


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
        vars=new Vector<var>();
    }

    public void SubscribeConcept(String concept)
    {

        IntentFilter intentFilter = new IntentFilter(concept);


        context.registerReceiver(this, intentFilter);



    }
    public abstract  void doAction();
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

    public void measure(String Concept,String name)
    {

    }
    public  void createVar(String name, var.VarType type, Object val)
    {
        var v=null;
        switch (type)
        {
            case Int:
                v=new var<Integer>(name,"int",(int)val);
                break;
            case String:
                v=new var<String>(name,"int",(String)val);
                break;

            case Char:
                break;
            case Double:
                v=new var<Double>(name,"int",(Double)val);
                break;
            case Null:
                break;


        }
        vars.add(v);

    }

    public var getVar(String name)
    {
        for(int i=0;i<vars.size();i++)
        {
            var v=vars.get(i);
            if(v.getName().equals(name))
                return v;
        }
        return  null;

    }
    //count how many times the concept apperas in  the DB
    public int count (String concept)
    {
        return  0;

    }

    /// NOT USED
    //=================
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
