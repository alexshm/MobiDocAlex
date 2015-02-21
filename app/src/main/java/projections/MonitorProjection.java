package projections;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import projections.monitoringObjects.TriggerCond;
import projections.monitoringObjects.monitorTriggerCondElement;


import static projections.monitoringObjects.monitorTriggerCondElement.*;



public class MonitorProjection extends  projection{
    public MonitorProjection(ProjectionType type, String projectionName, Context c) {
        super(ProjectionType.Monitor, projectionName, c);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //trigget the action
        //this.doAction();


        //get the name of the Intent
        boolean isReminder=intent.getAction().equals(this.ProjectionName+"_remainder");

        if(this.action!=null) {
            Log.i("projections.","trigger action successfully");

            this.InvokeAction(this.action,isReminder);

        }
        else {
            Log.i("projections.", "action is  nulll because....!!!");
            Log.i("projections.", "trigger happend!! from " + this.Type.toString() + " and the name is  : " + this.ProjectionName);
        }
    }

    @Override
    public void registerToTriggring() {
        IntentFilter intentFilter = new IntentFilter(this.ProjectionName);

        //register to triggring timer events
        Log.i("register to trigger", "register  to " + this.ProjectionName);
        context.registerReceiver(this, intentFilter);

        //register to remainder event
        IntentFilter RemainderintentFilter = new IntentFilter(this.ProjectionName+"_monitoring");

        context.registerReceiver(this, RemainderintentFilter);
    }
    public void startMonitor(ProjectionTimeUnit unit,int amout,ProjectionTimeUnit reamiderUnit,int reamiderAmount) {

        // TODO:  un ccomment this when projecction is ready
        //  super.SetDoActionEvery(unit,amout);
        //this.setReaminder(reamiderUnit,reamiderAmount);


        this.registerToTriggring();
        startProjection();



    }
    @Override
    public void doAction() {

        Action a=new Action(Action.ActionType.General,"MonitoringTest") {
            @Override
            public void doAction() {
                System.out.println("monitoring action!!!!!!!!!");
            }
        };
        a.createVar("abnormal", var.VarType.Int, 0);

        TriggerCond cond=new TriggerCond();
        cond.addCondition("one abnormal BP", ConditionActions.Count, MonitorOperators.GreatEqual,1);

    }


}
