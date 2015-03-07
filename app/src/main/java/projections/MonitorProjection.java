package projections;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


import projections.Actions.Action;
import projections.Actions.MonitorAction;


public class MonitorProjection extends  projection{


    protected MonitorAction triggerAction;

    public MonitorProjection(String projectionName, Context c) {
        super(ProjectionType.Monitor, projectionName, c);
        triggerAction=new MonitorAction(projectionName,c);
        hasAlarm=false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //get the name of the Intent
        boolean isMonitorTriggerHappened=intent.getAction().equals(this.ProjectionName+"_conditionTrigger");

        if(isMonitorTriggerHappened && this.action!=null) {
            Log.i("monitoring projections.","trigger action successfully");

            this.InvokeAction(false);
        }
        else {
            Log.i("projections.", "action is  nulll because....!!!");
            Log.i("projections.", "trigger happend!! from " + this.Type.toString() + " and the name is  : " + this.ProjectionName);
        }
    }

    public void defVar(String varName,String concept,var.VarType type)
    {
        if(this.triggerAction!=null)
            this.triggerAction.defineVar(varName,concept, type);
    }
    public void addValueConstraint(String varName,String concept, var.Operators op, String val)
    {
        if(this.triggerAction!=null)
            this.triggerAction.addValueConstraint(varName,concept,op,val);
    }

    public void setTimeConstraint( int daysAgo)
    {
        if(this.triggerAction!=null)
            this.triggerAction.setTimeConstraint(daysAgo);

    }
    public void setOpBetweenValueConstraints(String varName,var.OperationBetweenConstraint op)
    {
        if(this.triggerAction!=null)
            this.triggerAction.setOpBetweenValueConstraints(varName,op);

    }
    public void setAggregationConstraint(Action.AggregationAction action, Action.AggregationOperators op,int targetVal)
    {
        if(this.triggerAction!=null)
            this.triggerAction.setAggregationConstraint(action,op,targetVal);

    }
    public void setOpBetweenVars(var.OperationBetweenConstraint op)
    {
        if(this.triggerAction!=null)
            this.triggerAction.setOpBetweenVars(op);

    }

    @Override
    public void registerToTriggring() {

        //register to remainder event
        IntentFilter MonitoringFilter = new IntentFilter(this.ProjectionName+"_conditionTrigger");

        context.registerReceiver(this, MonitoringFilter);
    }



    public void setMonitoringVar()
    {
        ///the condition is :
        //  concept 5021 > 85
        // twice in 4 minutes

    }


    @Override
    public void doAction() {

    }


    @Override
    public void initProjection() {

        this.registerToTriggring();

    }


}
