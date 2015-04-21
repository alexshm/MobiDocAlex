package projections;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


import java.util.Vector;

import projections.Actions.Action;
import projections.Actions.MonitorAction;


public class MonitorProjection extends  projection{

    public MonitorProjection(String projectionName, String id,Context c) {
        super(ProjectionType.Monitor, projectionName,id, c);
        initMonitoringEvents();
        action=null;
        hasAlarm=false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("monitoring projection("+getProjectionId()+")","Receive data");
        //get the name of the Intent
        boolean isMonitorTriggerHappened=intent.getAction().equals(this.ProjectionName+"_conditionTrigger");
        receiveData(intent);

    }



    @Override
    public void registerToTriggring() {
        Log.i("Monitoring Projection","registerToTriggring");
        //register to remainder event
        IntentFilter MonitoringFilter = new IntentFilter(this.ProjectionName+"_conditionTrigger");

        context.registerReceiver(this, MonitoringFilter);
        Vector<IntentFilter> intentesToMonitor=new Vector<IntentFilter>();
        Vector<String> conceptsToMonitor=condAction.getConceptsToMonitor();

        for (int i=0;i<conceptsToMonitor.size();i++)
        {
            IntentFilter in = new IntentFilter(conceptsToMonitor.get(i));
            Log.i("Monitoring Projection","registering to concept: "+conceptsToMonitor.get(i));
            intentesToMonitor.add(in);
            context.registerReceiver(this, in);
        }


    }


    @Override
    public void initProjection() {

        this.registerToTriggring();

    }


}
