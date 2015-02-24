package projections;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


import static projections.monitoringObjects.valueConstraint.*;



public class MonitorProjection extends  projection{

    protected var v;
    public MonitorProjection(ProjectionType type, String projectionName, Context c) {
        super(ProjectionType.Monitor, projectionName, c);
        //v = new var("Ketanuria anbormal", "int", var.Operators.GreaterThen, 85);

    }

    @Override
    public void onReceive(Context context, Intent intent) {


        //get the name of the Intent
        boolean isReminder=intent.getAction().equals(this.ProjectionName+"_monitor");


     //  boolean ok= isSutisfiedVar(5);
        if(this.action!=null) {
            Log.i("projections.","trigger action successfully");

            this.InvokeAction(this.action,false);

        }
        else {
            Log.i("projections.", "action is  nulll because....!!!");
            Log.i("projections.", "trigger happend!! from " + this.Type.toString() + " and the name is  : " + this.ProjectionName);
        }
    }



    @Override
    public void registerToTriggring() {

        //register to remainder event
        IntentFilter MonitoringFilter = new IntentFilter(this.ProjectionName+"_monitor");

        context.registerReceiver(this, MonitoringFilter);
    }

    public void setMonitoringVar()
    {
        ///the condition is :
        //  concept 5021 > 85
        // twice in 4 minutes



    }
    public void startMonitor(ProjectionTimeUnit unit,int amout,ProjectionTimeUnit reamiderUnit,int reamiderAmount) {



        this.registerToTriggring();
        startProjection();



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
