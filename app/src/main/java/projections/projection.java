package projections;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.AsyncTask;
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

import com.google.android.gms.maps.Projection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import projections.Actions.Action;
import projections.Actions.MeasurementReminder;
import projections.Actions.MonitorAction;
import projections.Actions.compositeAction;
import projections.monitoringObjects.valueConstraint;
import projections.projectionParser.ActionParser;

/*=======================================================================

    represent a projection object.
    the projection is an abstract class . each type (cyclical /monitor) inherit from this class
    and implements few methods according to their logic.

    all the projection are extended BroadcastReceiver.
    each projection register to the BroadcastReceiver with the concepts he need for execution.
    when the GUI inserting a measurement value / answering question / accept reommendation etc..
    the projection class received the data and do its logic in  OnReceive  method . we override  the OnReceive method
    in  each projection type.
    in that way we can subscribe the projection for concepts and also can guaranty that a specific concept will be received
    in all the subscribed projections.

    the main process for incoming data is the following :
    ----------------------------------------------------
    when the projection created it subscribe for the needed concepts->  Gui inserted data-> Gui send the
    data with the required concept with SendBroadcast -> all the projection that registerd to that concept
     received tha data in OnReceive method ->  the data saved in the DataCollection -> check if need
     to trigger next action -> if yes-> search the next composite action  and trigger it.-> register to the
     new concepts of the next composite action -> send the actions to the GUI


    * compActionTable -each projection have a map of composite actions. this is helping us to execute
      any composite action we want.(i.e after receiving yes for question -> we need to invoke the composite action XXX)
      so to do that we just need to search in that map and invoke the execute method.

    *conceptsActionMap - saving the onReceive concepts. meaning the concepts we need to register for
     recommendation/ question action.if we have concepts in this list . we need to register to them when the projection
      initializing. moreover, these concepts are used for knowing wich action to invoke when recieved each concept .
      (i.e : if we add a recommnedation that the accept concept is 1234 and the decline concept is 3456. then with the
      help of this collection we know that if we recieved 1234 we need to invoke the action spacifed for the accept.
      moreover in that way we know to register the BroadcastReceiver  to 1234 and for 3456.

    *
    * condAction - an object that saved the conditions and vars . mainly used for monitor projection   or
                        for projection that need to check some values.
    *
    * action -  the current composite action that have been executed.
    *
    * actionToTrigger- the composite action to executed after the monitor is happend
    Main functions :
    -----------------


 ==========================================================================*/
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

      //  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
        String concept = intent.getStringExtra("concept");
        String val = String.valueOf(intent.getStringExtra("value"));
        Log.i("abstractProj("+getProjectionId()+")","receiveData:(concept:"+ concept+"value:"+val+")");
        String time = String.valueOf(intent.getStringExtra("time"));

        new receiveDataTask(this).execute(concept,val,time);

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

    /*

    */

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

    public void setOnReceiveConcept(String compositeActionNameForTrigger , String CurrentcompositeActionName, String concept,String onReceiveActionName,String onReceiveopertaion)
    {

        compositeAction curComp=compActionTable.get(CurrentcompositeActionName);
        curComp.addConceptForOnRecieve(concept);
        Log.i("abstractProj("+getProjectionId()+")","method: setOnReceiveConcept. register : "+CurrentcompositeActionName+"to the OnReceive concept : "+concept);

        compositeAction compForTrigger=compActionTable.get(compositeActionNameForTrigger);
        Log.i("abstractProj("+getProjectionId()+")","method: setOnReceiveConcept. set the OnReceive concept : "+concept+" for the composite action : "+compositeActionNameForTrigger);

        conceptsActionMap.put(concept,compForTrigger);

       //searching the spacific action in the composite Action
       // i.e : if for question action with name 'askpatient' we set the onReceive cocnept for 5111 for the yes answer.s
       // then we will search in the collecction the 'askpatient' name and set to him : askpatient.setOnReceiveConcept('yes','5111');


        Log.i("abstractProj("+getProjectionId()+")","method: setOnReceiveConcept. update the OnReceive for : "+onReceiveActionName+". sets the : "+onReceiveopertaion+" operation to concept : "+concept);
        if(onReceiveActionName!=""&& onReceiveopertaion!="") {
            for (Action a : curComp.actionsCollection)
                if (a.getActionName().equals(onReceiveActionName))
                    a.setOnReceiveConcept(onReceiveopertaion, concept);
        }




    }
    public void addActionToComposite(String compositeActionName,String type, String actionname, String actionConcept)
    {

        Action.ActionType acType=Utils.getActionType(type);

        ActionParser ap=new ActionParser(acType,context);
       String[] params={actionname,actionConcept};
        Log.i("abstractProj("+getProjectionId()+")","creating a "+acType.name()+" action");

       Action a= ap.parse(params);
        Log.i("abstractProj("+getProjectionId()+")","method: addActionToComposite.-add action("+a.getType().name()+","+a.getConcept()+","+a.getActionName()+")");
        Log.i("abstractProj("+getProjectionId()+")","method: addActionToComposite.-adding the action to compositeAction :"+compositeActionName);

        compActionTable.get(compositeActionName).addAction(a);


        if(getType().equals(ProjectionType.Cyclic) && acType.equals(Action.ActionType.Measurement)&& ((CyclicProjectionAbstract)this).remainderTime!=0) {
            Action remider = new MeasurementReminder("Reminder for "+a.getActionName());
            compActionTable.get(compositeActionName).addAction(remider);
            Log.i("abstractProj("+getProjectionId()+")","method: addActionToComposite.-adding the reminder for the  Measure action : "+a.getActionName());

        }

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


    public   void StopProjection() {



    }
    public void startAlarm()
    {
        return;
    }

    /*

    */

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
        if(getType().equals(ProjectionType.Cyclic))
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
    public   void stopProjection() {


        Log.i("projAbstract("+getProjectionId()+")","-method : stopping projection");
        this.abortBroadcast();
        Thread.yield();
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

    protected class receiveDataTask extends AsyncTask<String, Void, compositeAction> {

        private BroadcastReceiver proj;
        public receiveDataTask(BroadcastReceiver p)
        {
            proj=p;
        }

        @Override

        protected compositeAction doInBackground(String... params) {

            String concept = params[0];
            String value = params[1];
            String date = params[2];
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            boolean receiveConceptForQuestionOrRecomm = conceptsActionMap.containsKey(concept);
            if (receiveConceptForQuestionOrRecomm) {
                Log.i("Projectionabstract(" + getProjectionId() + ")", "receive 'onReceive' concept-> need to trigger the next action");

                compositeAction nextActionToexecute=conceptsActionMap.get(concept);
                Log.i("Projection("+getProjectionId()+")","receiveDataTask asyncTask- registerTo next action concepts");
                registerToNextActionConcepts(nextActionToexecute);
                return nextActionToexecute;
            }

            try {
                Date dateNow = sdf.parse(date);
                if (condAction != null) {
                    boolean okToInsert = condAction.isSatisfyVarsConditions(value);
                    if (okToInsert)
                        condAction.insertData(concept, value, dateNow);

                    //check if the value constraints+ time constraints is happening after Receiving
                    // the last data
                    if (condAction.isNeedToTrigger()) {
                        Log.i("Projection("+getProjectionId()+")","receiveDataTask asyncTask -registerTo condition trigger action concepts");
                        registerToNextActionConcepts(actionToTrigger);
                        return actionToTrigger;
                    }

                }
            } catch (ParseException e) {
                Log.e("projectionAbs(" + getProjectionId() + ")", "error parsing date in onReceive");
                return null;
            }
            return null;
        }

        private void registerToNextActionConcepts(compositeAction nextActionToExecute)
        {

            //register to remainder event

            Vector<IntentFilter> registerintentes =new Vector<IntentFilter>();
            Vector<String> conceptsToMonitor=nextActionToExecute.getAllConcepts();

            for (int i=0;i<conceptsToMonitor.size();i++)
            {
                IntentFilter in = new IntentFilter(conceptsToMonitor.get(i));
                Log.i("Projection("+getProjectionId()+")","registering to concept: "+conceptsToMonitor.get(i));
                registerintentes.add(in);
                context.registerReceiver(proj, in);
            }

        }

        @Override
        protected void onPostExecute(compositeAction result) {
            if(result==null)
                Log.i("abstractProj("+getProjectionId()+") ", "receiveDataTask - triggerEvent-there is no  action to be executed");
            else
            result.invoke(false);
        }
    }

}
