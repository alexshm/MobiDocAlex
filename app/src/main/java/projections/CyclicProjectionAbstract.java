package projections;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import projections.MeasurementAction;
import projections.projection.ProjectionTimeUnit;

public abstract class CyclicProjectionAbstract extends projection {

    private Object b;

    public CyclicProjectionAbstract(String projectionName, Context c) {
        super(ProjectionType.Cyclic, projectionName, c);

    }



    public  void setFrequency(ProjectionTimeUnit unit, int amout)
    {
        super.SetDoActionEvery(unit,amout);
    }
    public abstract void makeTestCyclic();

     public void startCyclic(ProjectionTimeUnit unit,int amout) {

         makeTestCyclic();
        this.setAlarmTrigger();

        this.registerToTriggring();
        startProjection();

        StartProjecction_alarm();

    }

    @Override
    public void registerToTriggring()
    {
        IntentFilter intentFilter = new IntentFilter(this.ProjectionName);

        Log.i("registerto trigger","reggister  tot "+this.ProjectionName);
        context.registerReceiver(this, intentFilter);
    }




    @Override
    protected void setAlarmTrigger() {
        Intent i=new Intent(this.ProjectionName);

        //context.sendBroadcast(i,android.Manifest.permission.VIBRATE);


        alarmInt= PendingIntent.getBroadcast(this.context, 0, i, 0);
    }
}
