package projections.projectionParser;

import android.content.Context;
import android.content.ContextWrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import projections.Actions.Action;
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
    private projection projectionToBuild;
    public parser(Context c)
    {

        // String _jsontest=
        cont=c;
    }

    public projection parse(String str)

    {
        final Pattern pattern = Pattern.compile("([a-z|A-Z]+)\\((.*)\\)");
        final Matcher m = pattern.matcher("");

        str = str.replaceAll("\\r\\n|\\r|\\n|\\t", "").trim();

        if (str!=null) {

            String[] commands=str.split(";");

            for( String command:commands)
            {
                m.reset(command);
                if(m.find())
                {
                    String comnd =m.group(1);
                    String params = m.group(2);
                    params= params.substring(1,params.length()-1);
                    String[] args=params.split("\",\"");
                    parseCommand(comnd,args);

                }
            }

        }

        return projectionToBuild;
    }

    // the command received from the projection file to be parsed
    private void parseCommand(String command,String[] args)
    {

        switch (command)
        {
            case "buildProjection":

                String projectionType = args[0];
                String projectionName = args[2];
                String id =args[1];
                generateProjection(projectionType, projectionName, id);
                break;

            case "setVar":

                String name = args[0];
                String concept = args[1];
                String type =args[2];
                addVar(name,concept,type);
                break;

            case "defineVar":
                String varName = args[0];
                String op="";
                String val="";

                char isOp=args[1].charAt(4);
                if(isOp=='=')
                    op=args[1].substring(3,4);
                else
                    op=args[1].substring(3,3);

                // if the value is string type
                if(args[1].contains("'"))
                    val=args[1].split("'")[1];
                else
                {
                    // if the value is int type
                    val=args[1].split(op)[1];
                }
                addVarExp(varName,op,val);

                break;

            case "setCondition":
                String opBetween = args[0].split(" ")[0];
                String aggregation = args[1];
                int timeConstraint=Integer.parseInt(args[2].split(" ")[0]);
                String aggregationAction=aggregation.split("()")[0];
                String aggregationOp="";
                String aggregationVal="";

                char isop=aggregation.split("()")[1].charAt(1);
                if(isop=='=')
                    aggregationOp=aggregation.split("()")[1].substring(0,1);
                else
                    aggregationOp=aggregation.split("()")[1].substring(0,0);

                aggregationVal=aggregation.split(aggregationOp)[1];
                int aggVal=Integer.parseInt(aggregationVal);

                addConditionAction(opBetween,aggregationAction,aggregationOp,aggVal,timeConstraint);
                break;

            case "onTriggerEvent":
                String mode = args[1];
                String actions = args[0];
                break;
            case "setFrequency":
                String repetFreq = args[0];
                int repeatAmout=Integer.parseInt(repetFreq.split(" ")[0]);
                String repeatUnit = repetFreq.split(" ")[1];
                String startTime=args[1];
                String remainder=args[2];
                int remainderAmout=Integer.parseInt(remainder.split(" ")[0]);
                String remainderUnit = remainder.split(" ")[1];

                setFreq(repeatAmout,repeatUnit,startTime,remainderAmout,remainderUnit);
                break;
            case "perform":
                String actionsarr=args[1];
                actionsarr=actionsarr.substring(1,actionsarr.length()-1);
                String action_mode=args[0];

                String [] actions_arr=actionsarr.split("\\\"\\),");

                preformAction(action_mode, actions_arr);
                break;

        }
    }
    /*
        create the projection according to the type
     */
    private void generateProjection( String type,String name,String id)
    {
        projection.ProjectionType projectionType=Utils.convertToProjectionType(type);

        //TODO: ADD id to projection
        switch(projectionType) {
            case Cyclic:
                projectionToBuild= new CyclicProjectionAbstract(name,cont,"");
                break;
            case Monitor:
                projectionToBuild=new MonitorProjection(name,cont);
                break;
            default:
                projectionToBuild= null;
        }

    }

    // build an action
    private Action buildAction(String type,String actionToParse )
    {
        String[] actionParms=actionToParse.split("\",\"");
        Utils.ActionType acType=Utils.getActionType(type);
        ActionParser ap=new ActionParser(acType,cont);
        return ap.parse(actionParms);

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
                Action a = buildAction(actionType, actionToParse);
                projectionToBuild.addAction(a);
            }
        }
        //sets the mode of all the actions ( seqeuncially/parralell)

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

        projectionToBuild.setAggregationConstraint(aggAction,aggOp,aggregateVal);

        projectionToBuild.setTimeConstraint(timeConstraint);

        if(opbetween.equals("and"))
            projectionToBuild.setOpBetweenVars(var.OperationBetweenConstraint.And);

    }

    //  add a variable that descibes a condition for monitoring like :
    // 2 abnormal BP in the last 5 days
    private void addVar(String varName,String concept,String type)
    {
        var.VarType varType=Utils.getVarType(type);
        projectionToBuild.defVar(varName,concept,varType);

    }

    private void addVarExp(String varName,String op,String val)
    {
        var.Operators varOp=Utils.getVarOp(op);

        projectionToBuild.addValueConstraint(varName,varOp,val);
    }

    // sets the frequency to activates the action in the cyclic projection
    // (i.e every 5 days at 8:00) and also set the hour to be triggered(8:00 is startTime)
    private void setFreq(int repeatAmout,String repeatUnit,String startTime,int remainderAmout,String remainderUnit)
    {
        if(projectionToBuild.getType().equals(projection.ProjectionType.Cyclic))
        {
            projection.ProjectionTimeUnit repUnit=Utils.getTimeUnit(repeatUnit);
            projection.ProjectionTimeUnit remUnit=Utils.getTimeUnit(remainderUnit);
            ((CyclicProjectionAbstract) projectionToBuild).setStartTime(startTime);
            ((CyclicProjectionAbstract)projectionToBuild).setReaminder(remUnit,remainderAmout);
            ((CyclicProjectionAbstract)projectionToBuild).setFrequency(repUnit,repeatAmout);

        }

    }







}
