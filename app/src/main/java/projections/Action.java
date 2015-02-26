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
import ch.lambdaj.function.aggregate.Avg;
import ch.lambdaj.function.aggregate.Sum;
import projections.monitoringObjects.DataCollection;
import projections.monitoringObjects.valueConstraint;

import static ch.lambdaj.Lambda.avg;
import static ch.lambdaj.Lambda.count;
import static ch.lambdaj.Lambda.sum;

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
    protected  var.OperationBetweenConstraint betweenVars;
    protected AggregationOperators aggregationOperator;
    public Messenger MessengerToMonitoringService=null;
    public int aggregationTargetVal;
    protected boolean mIsBound=false;
    protected Vector<var> vars;
    protected DataCollection data;
    public   Intent serviceIntent =null;
    protected AggregationAction aggregationAction;
    private static final int NO_VAR = 1;
    private static final int CYCLIC = 2;
    private static final int MONITOR = 3;

    public enum AggregationAction
    {
        Sum,Avg,Count
    }
    public enum  AggregationOperators
    {
        Equal,GreaterThen,LessThen,GreatEqual,LessEqual
    }
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
        aggregationAction=null;
        aggregationTargetVal=0;
        aggregationOperator=null;
        betweenVars= var.OperationBetweenConstraint.Or;
        serviceIntent  = new Intent(_context, projections.monitoringObjects.MonitoringDBservice.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
        String concept=intent.getStringExtra("concept");
        String val=String.valueOf(intent.getStringExtra("value"));
        Log.i("geting value ","get value of : "+val);
        String time=String.valueOf(intent.getStringExtra("time"));

        try {
            Date dateNow=sdf.parse(time);


            boolean okToInsert=isSatisfyVarsConditions(val);
            if(okToInsert)
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


    public void defineVar(String name,String concept,var.VarType type)
    {
        var v=null;
        switch (type)
        {
            case Int:
                v=new var<Integer>(name,concept,type);
                break;
            case String:
                 v=new var<String>(name,concept,type);
                break;

            case Char:
                break;
            case Double:
                 v=new var<Double>(name,concept,type);
                break;
            case Null:
                break;
        }
        vars.add(v);
    }


    public void setOpBetweenValueConstraints(String varName, var.OperationBetweenConstraint op)
    {
        var v =getVar(varName);
        v.setOpBetweenValueConstraints(op);
    }
    public void setAggregationAction(AggregationAction action,AggregationOperators op,int targetVal)
    {
        aggregationAction=action;
        aggregationOperator=op;
        aggregationTargetVal=targetVal;
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
            System.out.println("data in "+i+" is "+data.getDataItems().get(i).getVal());
    }

    private boolean isSatisfyVarsConditions(String val)
    {
        if(betweenVars.equals(var.OperationBetweenConstraint.And))
        {
            boolean ans=true;
            for(var v:vars) {
                ans = ans && v.isSatisfyVar(val);
            }
            return  ans;
        }
        else {
            boolean ans = false;
            for(var v:vars) {
                ans = ans ||v.isSatisfyVar(val);
            }
            return ans;
        }

    }
    public boolean isSatisfyAggregationConstraint(Iterable data)
    {

        int ans=AggregationFunc(data);
        System.out.println("the func  is : "+  ans);
        return (ans>=aggregationTargetVal);

    }
    public int AggregationFunc(Iterable data)
    {
        switch (aggregationAction)
        {
            case Sum:
                return  sum(data).intValue();

            case Avg:
                return avg(data).intValue();

            case Count:

                return count(data).size();

        }
        return -1;
    }
    public boolean isNeedToTrigger()
    {
        boolean ans=false;
        if(vars.size()>0 || data.hasValueConstraint())
        {
           Iterable it=data.getDataValues();

            boolean needToTrigger=isSatisfyAggregationConstraint(it);
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
        var v=getVar(varName);
        v.addValueConstraint(concept, op, val);

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

    protected void setAggregationConstraint(String varName, AggregationAction action, AggregationOperators op,int targetVal)
    {
        if(vars.size()>0)
            setAggregationAction(action,op,targetVal);
    }

    //By default the Operation between Vars is OR
    public void setOpBetweenValueConstraints(var.OperationBetweenConstraint op)
    {
        betweenVars=op;
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
