package projections.projectionParser;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import projections.Actions.Action;
import projections.Actions.MeasurementReminder;
import projections.CyclicProjectionAbstract;
import projections.MonitorProjection;
import projections.Utils;
import projections.projection;
import projections.var;

/**
 * Created by Moshe on 3/13/2015.
 */
public class parser {


    private Context cont;
    private String freqTime;
    private String freqUnit;
    private String startTimeTxt;
    private String remTime;
    private String remUnit;
    private projection projectionToBuild;
    public parser(Context c)
    {

    }

    public projection parse(String str)

    {

        return projectionToBuild;
    }

    // the command received from the projection file to be parsed
    private void parseCommand(String command,String[] args)
    {


    }

    /*
    =================================================
      parse the action from the given projection and
      add its to the projection
    =================================================
   */
    private void preformAction(String executionMode,String[] actions) {
        Utils.ExecuteMode mode=Utils.convertToExecuteMode(executionMode);
        projectionToBuild.setExectuionMode(mode);

        for (String action : actions) {
            action = action.replaceAll("\\\\", "");
            final Pattern pattern = Pattern.compile("([a-z|A-Z]+)\\((.*)\\)");
            final Matcher m = pattern.matcher("");
            m.reset(action);
            //parse the action from the given projection and
            // adds it to the built projection
            if (m.find()) {
                String actionType = m.group(1);
                String actionToParse = m.group(2);
                actionToParse=actionToParse.substring(1,actionToParse.length()-1);
                Action a=null;//; = buildAction(actionType, actionToParse);
                projectionToBuild.addAction(null);
                // in case the action mesure and we have remidnder
              //  addRemminderAction(a.getType(),a.getActionName());

            }
        }

    }


    /*
    =================================================
    add to the projection a condition
     - opbetween is the op between all the vars defined
     - aggregateAction    sum/avg/count
     - aggregateOp is = / <= / < / > / >=
     - aggregateVal  the target val the condtion need to be
     (i.e if aggregateVal=2 and action action = count -> then the condition waits until the count
      for a certain concept is equal to 2)
     -timeConstraint - how long backwards to remmembers data(i.e 7 days , 2 weeks...)
     =================================================\
    */
    private void addConditionAction(String opbetween,String aggregateAction,String aggregateOp,int aggregateVal ,int timeConstraint)
    {

        Action.AggregationOperators aggOp=Utils.getAggregationOp(aggregateOp);

        Action.AggregationAction aggAction=Utils.getAggregationAction(aggregateAction);

       // projectionToBuild.setAggregationConstraint(aggAction,aggOp,aggregateVal);

        projectionToBuild.setTimeConstraint(timeConstraint);

        if(opbetween.equals("and"))
            projectionToBuild.setOpBetweenVars(var.OperationBetweenConstraint.And);

    }

    //  add a variable that descibes a condition for monitoring like :
    // 2 abnormal BP in the last 5 days
    private void addVar(String varName,String concept,String type)
    {
        var.VarType varType=Utils.getVarType(type);
        projectionToBuild.defVar(varName,concept,type);

    }

    private void addVarExp(String varName,String op,String val)
    {
        var.Operators varOp=Utils.getVarOp(op);

       // projectionToBuild.addValueConstraint(varName,varOp,val);
    }

    // sets the frequency to activates the action in the cyclic projection
    // (i.e every 5 days at 8:00) and also set the hour to be triggered(8:00 is startTime)
    private void setFreq(int repeatAmout,String repeatUnit,String startTime,int remainderAmout,String remainderUnit)
    {


    }



}
