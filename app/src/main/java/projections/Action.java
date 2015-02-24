package projections;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import ch.lambdaj.Lambda;
import projections.monitoringObjects.DataCollection;
import projections.monitoringObjects.valueConstraint;

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

    public Messenger MessengerToMonitoringService=null;
    protected boolean mIsBound=false;
    protected Vector<var> vars;
    protected DataCollection data;
    public   Intent serviceIntent =null;

    private static final int NO_VAR = 1;
    private static final int CYCLIC = 2;
    private static final int MONITOR = 3;

    public enum ActionType {
        Question, Recommendation, Notification ,Measurement,General
        ,Remainder,Trigger
    }
   public Action(ActionType  _type,String name, String concept,Context _context)
    {
        count=0;
        actionName=name;
        type=_type;
        actionConcept=concept;
        ansVal="";
        context=new ContextWrapper(_context);
        data= new DataCollection(concept,2);
        vars=new Vector<var>();
        serviceIntent  = new Intent(_context, projections.monitoringObjects.MonitoringDBservice.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
        String concept=intent.getStringExtra("concept");
        String val=String.valueOf(intent.getStringExtra("value"));

        Date dateNow=new Date();

        String now = sdf.format(dateNow);
        try {
            dateNow=sdf.parse(now);

            data.insertItem(concept,val,dateNow);

        } catch (ParseException e) {
            Log.e("Action","error parsing date in onReceive");
        }

        //check if the value constraints+ time constraints is happening after Receiving
        // the last data
        if(isNeedToTrigger())
        {
            Intent i = new Intent(actionName+"_conditionTrigger");
            context.sendBroadcast(i,android.Manifest.permission.VIBRATE);
            Log.i("Action On recive","trigger the conditin trigger : "+actionName+"_conditionTrigger");
        }

        //TODO:  save the dataitem to DB or in file in SDCARD using service :  MonitoingDB service
        /*
         //saving the data to DB using service
        ///========================================
        Message msg=null;
        Bundle bundle = new Bundle();
        if (vars.size()>0 ) {
            // the value is abnormal
            msg = Message.obtain(null, MONITOR, 0, 0, 0);
            bundle.putString("value", val);
            bundle.putString("concept", concept);
            bundle.putString("var",vars.get(0).getOperator().toString()+"#"+vars.get(0).getVal().toString());
        }
       else {
            // reciving regular mesurment with no if or monitoring
            msg = Message.obtain(null, NO_VAR, 0, 0, 0);
            bundle.putString("value", val);
            bundle.putString("concept", concept);
        }
            msg.setData(bundle);
            try {
                if(msg!=null)
                    MessengerToMonitoringService.send(msg);
                else
                    Log.e("projection.InvokeAction","MSG is null");
            } catch (RemoteException e) {
                Log.e("projection.InvokeAction","error sending msg: "+msg.getData().getString("value"));
        }
        */
    }
    public void defineVar(String name,var.VarType type)
    {
        var v=null;
        switch (type)
        {
            case Int:
                v=new var<Integer>(name,"5021",type);
                break;
            case String:
                 v=new var<String>(name,"5021",type);
                break;

            case Char:
                break;
            case Double:
                 v=new var<Double>(name,"5021",type);
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
    public void printDataCollections()
    {
        System.out.println("printong data");
        System.out.println("--------------------------------------");
        for(int i=0;i<data.getDataItems().size();i++)
            System.out.println("data in "+i+" is "+data.getDataItems().get(0).getVal());
    }
    public boolean isNeedToTrigger()
    {
        boolean ans=false;
        if(vars.size()>0 || data.hasValueConstraint())
        {
            //TODO:  for more then 1 var

            var v=vars.get(0);
            Iterable it=data.getDataValues();
            boolean needToTrigger=v.isSatisfyAggregationonstraint(it);
            if(needToTrigger) {
                    ans=true;
                Log.i("Action","the Conditions  is happend -> apply the triggering");
            }
        }
        return ans;
    }


    public   void addValueConstraint(String varName,String concept, var.Operators op, String val)
    {
        valueConstraint valc=new valueConstraint(concept,op,val);
        data.setValueConstraint(valc);
        vars.get(0).addValueConstraint(concept,op,val);

    }

    public void setTimeConstraint( int daysAgo)
    {
        data.setTimeConstraint(daysAgo);

    }
    protected ServiceConnection mconnection= new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MessengerToMonitoringService=new Messenger(service);
            mIsBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MessengerToMonitoringService=null;

            mIsBound=false;
        }
    };

    public   void startMonitoring() {

        if (!mIsBound) {

            context.bindService(serviceIntent, mconnection, Context.BIND_AUTO_CREATE);
            this.mIsBound = true;

        }
    }



    public void setAggregationConstraint(String varName, var.AggregationAction action, var.Operators op,int targetVal)
    {
        if(vars.size()>0)
            vars.get(0).setAggregationAction(action,op,targetVal);
    }


    public void SubscribeConcept(String concept)
    {

        IntentFilter intentFilter = new IntentFilter(concept);

        context.registerReceiver(this, intentFilter);
        startMonitoring();


    }
    public abstract  void doAction();
    public void UnSubscribeConcept(String concept)
    {
        context.unregisterReceiver(this);

        ///STOP MONITORING SERVICE
        if (mIsBound) {
            mconnection = null;
            mIsBound = false;
            context.stopService(serviceIntent);
            Toast.makeText(context, "service succefully stopped", Toast.LENGTH_LONG).show();
        }
    }
    //send the action to the Mobile Gui
    public Message getActionToSend(boolean isReminder)
    {

        msgToSend=actionName;
        int msgType=type.ordinal()+1;

        // in case of raminder msg
        if (isReminder)
            msgType=ActionType.Remainder.ordinal()+1;

        Message msg = Message.obtain(null,msgType,0,0,0);
        Bundle bundle = new Bundle();


        bundle.putString("value", msgToSend);
        msg.setData(bundle);
        System.out.println("the data when build is : "+msg.getData().getString("value")+" "+msgType);
        return  msg;


    }



}
