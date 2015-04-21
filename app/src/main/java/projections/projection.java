package projections;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.content.ComponentName;
import android.content.ServiceConnection;

import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import projections.Actions.Action;
import projections.Actions.MeasurementReminder;
import projections.Actions.MonitorAction;
import projections.Actions.compositeAction;
import projections.monitoringObjects.valueConstraint;
import projections.projectionParser.ActionParser;


public abstract class projection extends BroadcastReceiver implements Runnable{


    protected ProjectionType Type;

    protected String ProjectionName;

    public Context context;

    protected Hashtable<String, compositeAction> compActionTable;

    // map for saving the onReceive concepts ** only for question/recommendations
    // and not for all the actions. TODO: not sure the currentCompositeAction need to be saved , maybe just the concept
    // the structure : oncept,compositeAction
    //concept is the primary key
    protected Hashtable<String, compositeAction> conceptsActionMap;

    protected String Id;
    protected String currentCompositeAction;
    protected MonitorAction condAction;
    protected boolean hasAlarm;
    protected compositeAction action;
    protected compositeAction actionToTrigger;
    protected boolean mIsBound = false;
    protected Utils.ExecuteMode mode;

    public enum ProjectionType {
        Cyclic, Monitor
    }

    public enum ProjectionTimeUnit {
        Second, Minute, Hour, Day, Week, Month, None
    }

    public projection(ProjectionType type,String _ProjectionName,String id,Context _context)
    {
        Type=type;
        Id=id;
        ProjectionName=_ProjectionName;
        context =_context;
        action=new compositeAction(context, Utils.ExecuteMode.Sequential);
        mode= Utils.ExecuteMode.Sequential;
        hasAlarm=false;
        condAction=null;
        compActionTable=new Hashtable<String, compositeAction>();
        currentCompositeAction="";
        conceptsActionMap=new Hashtable<>();
        actionToTrigger=null;

    }


    protected void receiveData(Intent intent) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
        String concept = intent.getStringExtra("concept");
        String val = String.valueOf(intent.getStringExtra("value"));
        Log.i("abstractProj("+getProjectionId()+")","receiveData:(concept:"+ concept+"value:"+val+")");
        String time = String.valueOf(intent.getStringExtra("time"));

        try {
            Date dateNow = sdf.parse(time);

            // apply the next action to trigger when recieve concepts regarding to recommendation/qeustion
            //if needed
            if(this.conceptsActionMap.containsKey(concept)) {
                Log.i("Projectionabstract(" + getProjectionId() + ")", "receive 'onReceive' concept-> need to trigger the next asction");
                this.conceptsActionMap.get(concept).invoke(false);
            }

            if (condAction != null) {
                boolean okToInsert = condAction.isSatisfyVarsConditions(val);
                if (okToInsert)
                    this.condAction.insertData(concept, val, dateNow);

                //check if the value constraints+ time constraints is happening after Receiving
                // the last data
                if (condAction.isNeedToTrigger()) {
                    Log.i("Action On recive", "trigger the conditin trigger ");
                    triggerEvent();
                }



            }

        } catch (ParseException e) {
            Log.e("projectionAbs("+getProjectionId()+")", "error parsing date in onReceive");
        }


    }


    public  String getProjectionName()
    {
        return ProjectionName;
    }

    public  String getProjectionId()
    {
        return Id;
    }
    public  ProjectionType getType()
    {
        return Type;
    }


    public void  setExectuionMode(Utils.ExecuteMode executeMode)
    {
        mode=executeMode;
        if(executeMode.equals(Utils.ExecuteMode.Parallel))
            action.setExecuteMode(mode);
    }

    public void initMonitoringEvents() {
        condAction = new MonitorAction(context);
    }


    public void initTriggerActions(Utils.ExecuteMode mode) {
        actionToTrigger = new compositeAction(context, mode);
    }

    public void addActionToTrigger(Action a)
    {
        if (actionToTrigger!=null)
        {
            actionToTrigger.addAction(a);
        }
    }

    public void addNewCompositeAction(String compositeActionName,String ExMode)
    {
        Utils.ExecuteMode mode=Utils.convertToExecuteMode(ExMode);
        compositeAction ca=new compositeAction(context,mode);
        compActionTable.put(compositeActionName,ca);
        Log.i("abstractProj("+getProjectionId()+")","method: addNewCompositeAction.  creating new composite action in name : "+compositeActionName);
    }

    public void setOnReceiveConcept(String compositeActionNameToTrigger, String concept)
    {
        compositeAction comp=compActionTable.get(compositeActionNameToTrigger);
        comp.addConceptForOnRecieve(concept);
        conceptsActionMap.put(concept,comp);

    }
    public void addActionToComposite(String compositeActionName,String type, String actionname, String actionConcept)
    {

        Action.ActionType acType=Utils.getActionType(type);

        ActionParser ap=new ActionParser(acType,context);
       String[] params={actionname,actionConcept};

       Action a= ap.parse(params);
        Log.i("abstractProj("+getProjectionId()+")","method: addActionToComposite.-add action("+a.getType().name()+","+a.getConcept()+","+a.getActionName()+")");
        Log.i("abstractProj("+getProjectionId()+")","method: addActionToComposite.-adding the action to compositeAction :"+compositeActionName);

        compActionTable.get(compositeActionName).addAction(a);

        /*TODO: add remidner action --
        if(acType.equals(Action.ActionType.Measurement) && this.rremTime!="0") {
            Action remider = new MeasurementReminder(reminderTxt);
            projectionToBuild.addAction(remider);
        }
            */
    }

    public void addActionToComposite(String compositeActionName,Action a)
    {
        compActionTable.get(compositeActionName).addAction(a);

    }

    public void addAction(Action a)
    {

       action.addAction(a);
    }

    public abstract  void registerToTriggring();


    public boolean Isbound()
    {
        return mIsBound;
    }

	
	public abstract void doAction();


    public   void StopProjection() {



    }
    public void startAlarm()
    {
        return;
    }

    public void setTriggerAction(String triggerActionName)
    {
        Log.i("abstractproj","setting trigger action to :"+triggerActionName);
        this.actionToTrigger=compActionTable.get(triggerActionName);
        Log.i("abstractproj","the new  trigger action to size:"+actionToTrigger.actionsCollection.size());
    }
    public void defVar(String varName,String concept,String  type)
    {
        var.VarType varType=Utils.getVarType(type);

        if(this.condAction!=null)
        {
            Log.i("abstractProj("+getProjectionId()+")","defVar-setting var name : "+varName+" for concept : "+concept);
            this.condAction.defineVar(varName,concept, varType);
        }

    }
    public void addValueConstraint(String varName, String op, String val)
    {
        var.Operators varOp=Utils.getVarOp(op);

        if(this.condAction!=null)
        {
            Log.i("abstProj","adding val constraint ("+varName+","+varOp.name().toString()+","+val);
            this.condAction.addValueConstraint(varName,varOp,val);
        }

    }

    public void setTimeConstraint( int daysAgo)
    {
        if(this.condAction!=null)
            this.condAction.setTimeConstraint(daysAgo);

    }
    public void setOpBetweenValueConstraints(String varName,var.OperationBetweenConstraint op)
    {
        if(this.condAction!=null)
            this.condAction.setOpBetweenValueConstraints(varName,op);

    }
    public void setAggregationConstraint(String operation,String operator,int targetVal)
    {
        Action.AggregationAction oper= Utils.getAggregationAction(operation);
        Log.i("setAggregationConstrai","setting the aggaction for:"+oper.name().toString());
        Action.AggregationOperators aggoperator=Utils.getAggregationOp(operator);
        Log.i("setAggregationConstrain","setting the AggregationOperators for:"+aggoperator.name().toString());
        if(this.condAction!=null) {

            this.condAction.setAggregationConstraint(oper, aggoperator, targetVal);
        }

    }
    public void setOpBetweenVars(var.OperationBetweenConstraint op)
    {
        if(this.condAction!=null)
            this.condAction.setOpBetweenVars(op);

    }
    public void onStart(String compositeName)
    {
        this.currentCompositeAction=compositeName;
        if(getType().equals(ProjectionType.Monitor))
        {

        }
        else
        {
            //when the type is Cyclic
            action=compActionTable.get(this.currentCompositeAction);
        }


    }


    @Override
    public void run() {
        startProjection();
    }

    public   void startProjection() {

       // action=compActionTable.get(this.currentCompositeAction);
        Log.i("projAbstract("+getProjectionId()+")","-method : startProjection --set starting from action : "+this.currentCompositeAction);
        initProjection();
        if(hasAlarm)
            this.startAlarm();


    }


    public void initMonitorAction()
    {
        condAction=new MonitorAction(context);
        Log.i("abstractProj("+getProjectionId()+")","initiazing Monitor Action ");
    }
    public  abstract void initProjection();

    public void InvokeAction(boolean isReminder) {

        }
    protected void triggerEvent() {
        if(this.actionToTrigger!=null)
            this.actionToTrigger.invoke(false);
        else
        Log.i("abstractProj("+getProjectionId()+") ", "triggerEvent-the action to trigger is null");
    }

}
