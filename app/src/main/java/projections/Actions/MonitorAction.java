package projections.Actions;


import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.Vector;

import projections.monitoringObjects.DataCollection;
import projections.monitoringObjects.valueConstraint;
import projections.var;

import static ch.lambdaj.Lambda.avg;
import static ch.lambdaj.Lambda.count;
import static ch.lambdaj.Lambda.sum;
/*
  class that represent  monitoring operation.

  NEED TO CONTINUE
 ===========================================================================
 * AggregationAction - ( Sum, Avg, Count) - actions to accoumlate if we have conditions
                    (i.e we want to count how many abnormal BP we had)

 * AggregationOperators - (Equal, GreaterThen, LessThen, GreatEqual, LessEqual)
 *                          =, > , < , >= , <=
     operations for the aggregation (i.e we want to trigger something only if
     the count for abnormal BP is greaterthan(>) 4)


  */
public class MonitorAction extends  Action {


    protected String ansVal;

    protected String msgToSend;

    public Messenger MessengerToMonitoringService = null;

    public int aggregationTargetVal;
    protected boolean mIsBound = false;
    //=============================
    protected Vector<var> vars;
    protected DataCollection data;

    protected int count;
    protected var.OperationBetweenConstraint betweenVars;
    protected AggregationOperators aggregationOperator;

    private Vector<String> conceptsToMonitor;
    //=============================
    public Intent serviceIntent = null;
    protected AggregationAction aggregationAction;

    protected boolean isReminder;


    public MonitorAction(Context _context) {
        super(ActionType.Trigger, "", "", _context);
        setType(ActionType.Trigger);
        count = 0;

        ansVal = "";
        conceptsToMonitor=new Vector<String>();
        data = new DataCollection( 1);
        vars = new Vector<var>();
        aggregationAction = null;
        aggregationTargetVal = 0;
        aggregationOperator = null;
        betweenVars = var.OperationBetweenConstraint.Or;

        serviceIntent = new Intent(_context, projections.monitoringObjects.MonitoringDBservice.class);
        isReminder=false;

        //===========================

        Log.i("MonitorAction","creating monitoring Action");
    }

    @Override
    public void setOnReceiveConcept(String onReceiveOp,String concept) {

    }
    protected ServiceConnection mconnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MessengerToMonitoringService = new Messenger(service);
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MessengerToMonitoringService = null;

            mIsBound = false;
        }
    };

    public void defineVar(String name, String concept, var.VarType type) {
        var v = null;
        switch (type) {
            case Int:
                v = new var<Integer>(name, concept, type);
                break;
            case String:
                v = new var<String>(name, concept, type);
                break;

            case Char:
                break;
            case Double:
                v = new var<Double>(name, concept, type);
                break;
            case Null:
                break;
        }
        vars.add(v);


        Log.i("MonitorAction","adding var to monitor(concept:"+concept+")");
        //add the concept to the monitoring comcepts
        addConceptToMonitor(concept);
    }
    public void setOpBetweenValueConstraints(String varName, var.OperationBetweenConstraint op) {
        var v = getVar(varName);
        v.setOpBetweenValueConstraints(op);
    }

    public void setAggregationAction(AggregationAction action, AggregationOperators op, int targetVal) {
       aggregationAction = action;
        aggregationOperator = op;
        aggregationTargetVal = targetVal;
    }
    public void insertData(String concept,String val,Date dateNow)
    {

        data.insertItem(concept, val, dateNow);
    }
    public var getVar(String name) {
        for (int i = 0; i < vars.size(); i++) {
            var v = vars.get(i);
            if (v.getName().equals(name))
                return v;
        }
        return null;

    }

    public Vector<String>getConceptsToMonitor()
    {
        return conceptsToMonitor;
    }
    public void printDataCollections() {
        System.out.println("printong data");
        System.out.println("--------------------------------------");
        for (int i = 0; i < data.getDataItems().size(); i++)
            System.out.println("data in " + i + " is " + data.getDataItems().get(i).getVal());
    }

    public boolean isSatisfyVarsConditions(String val) {

        if (betweenVars.equals(var.OperationBetweenConstraint.And)) {

            boolean ans = true;
            for (var v : vars) {
                ans = ans && v.isSatisfyVar(val);
            }

            return ans;
        } else {

            boolean ans = false;
            for (var v : vars) {
                ans = ans || v.isSatisfyVar(val);
            }

            return ans;
        }

    }

    public boolean isSatisfyAggregationConstraint(Iterable data) {

        int ans = AggregationFunc(data);
        Log.i("Monitor action","the result of the  AggregationConstraint is : " + ans);
        return (ans >= aggregationTargetVal);

    }

    public int AggregationFunc(Iterable data) {

        switch (aggregationAction) {
            case Sum:
                return sum(data).intValue();

            case Avg:
                return avg(data).intValue();

            case Count:

                return count(data).size();

        }
        return -1;
    }

    public boolean isNeedToTrigger() {
        boolean ans = false;

        if (vars.size() > 0 || data.hasValueConstraints()) {
            Iterable it = data.getDataValues();

            boolean needToTrigger = isSatisfyAggregationConstraint(it);
            if (needToTrigger) {
                ans = true;
                data.getDataValues();
                Log.i("Action", "the Conditions  is happend -> apply the triggering");
            }
        }
        return ans;
    }


    public void addValueConstraint(String varName, var.Operators op, String val) {
        var v = getVar(varName);
        String concept=v.getConcept();
        valueConstraint valc = new valueConstraint(concept, op, val);
        data.addValueConstraint(concept,valc);

        v.addValueConstraint(concept, op, val);

    }

    public void setTimeConstraint(int daysAgo) {
        Log.i("Monitoring Action","setting the days to remmenber in the DataCollection to :"+daysAgo);
        data.setTimeConstraint(daysAgo);



    }



    public void setAggregationConstraint(AggregationAction action, AggregationOperators op, int targetVal) {
        if (vars.size() > 0)
            setAggregationAction(action, op, targetVal);
    }

    //By default the Operation between Vars is OR
    public void setOpBetweenVars(var.OperationBetweenConstraint op) {
        betweenVars = op;
    }


    public void addConceptToMonitor( String conceptId)
    {
        Log.i("Monitor action","need to add concept : "+"conceptId to the list of concepts : "+!conceptsToMonitor.contains(conceptId));
        if(!conceptsToMonitor.contains(conceptId))
       conceptsToMonitor.add(conceptId);

    }

    @Override
    public Message call() throws Exception {
        return null;
    }
}
