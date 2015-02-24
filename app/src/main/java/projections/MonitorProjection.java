package projections;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


import static projections.monitoringObjects.valueConstraint.*;



public class MonitorProjection extends  projection{

    protected var v;
    protected  MonitorAction triggerAction;

    public MonitorProjection(String projectionName, Context c) {
        super(ProjectionType.Monitor, projectionName, c);
        triggerAction=new MonitorAction(projectionName,"5021",c);


    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //get the name of the Intent
        boolean isMonitorTriggerHappened=intent.getAction().equals(this.ProjectionName+"_conditionTrigger");


     //  boolean ok= isSutisfiedVar(5);
        if(isMonitorTriggerHappened && this.action!=null) {
            Log.i("monitoring projections.","trigger action successfully");

            this.InvokeAction(this.action,false);
        }
        else {
            Log.i("projections.", "action is  nulll because....!!!");
            Log.i("projections.", "trigger happend!! from " + this.Type.toString() + " and the name is  : " + this.ProjectionName);
        }
    }

    public void defVar(String varName,var.VarType type)
    {
        if(this.triggerAction!=null)
            this.triggerAction.defineVar(varName, type);
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
    public void setAggregationConstraint(String varName, var.AggregationAction action, var.Operators op,int targetVal)
    {
        if(this.triggerAction!=null)
            this.triggerAction.setAggregationConstraint(varName,action,op,targetVal);

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
    public void startMonitor()
    {
        this.action=new MeasurementAction("mesure after testing","1111",context);
        defVar("Ketanuria anbormal", var.VarType.Int);
        addValueConstraint("Ketanuria anbormal", "5021", var.Operators.GreaterThen, "85");
        setAggregationConstraint("Ketanuria anbormal", var.AggregationAction.Count, var.Operators.GreaterThen,2);
        this.registerToTriggring();
        startProjection();
    }


    public void startMonitor(ProjectionTimeUnit unit,int amout,ProjectionTimeUnit reamiderUnit,int reamiderAmount) {

    }
    @Override
    public void doAction() {

        /*
        Action a=new Action(Action.ActionType.General,"MonitoringTest") {
            @Override
            public void doAction() {
                System.out.println("monitoring action!!!!!!!!!");
            }
        };
        a.createVar("abnormal", var.VarType.Int, 0);

        TriggerCond cond=new TriggerCond();
        cond.addCondition("one abnormal BP", ConditionActions.Count, MonitorOperators.GreatEqual,1);
        */
    }


}
