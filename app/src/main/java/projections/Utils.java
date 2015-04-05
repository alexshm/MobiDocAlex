package projections;


import java.util.Properties;

import example.com.mobidoc.ConfigReader;
import projections.Actions.Action;


public  final class Utils {


    static int intentCounter;
    public static enum AggregationAction {
        Sum, Avg, Count
    }

    public static enum AggregationOperators {
        Equal, GreaterThen, LessThen, GreatEqual, LessEqual
    }

    public static enum ActionType {
        Question, Recommendation, Notification, Measurement, General, Remainder, Trigger,CallBack
    }

    public static enum Actor {
        Patient, physician
    }
    public static enum ExecuteMode {
        Parallel, Sequential
    }
    public static enum ProjectionType {
        Cyclic, Monitor
    }

    public enum ProjectionTimeUnit {
        Second, Minute, Hour, Day, Week,Month ,None
    }

    public static synchronized int getIntentCounter()
    {
        intentCounter++;
        int ans=intentCounter;

        return ans;
    }
    public static projection.ProjectionTimeUnit getTimeUnit(String type) {
        switch(type) {
            case "sec":
                return projection.ProjectionTimeUnit.Second;
            case "minute":
                return projection.ProjectionTimeUnit.Minute;
            case "hour":
                return projection.ProjectionTimeUnit.Hour;
            case "day":
                return projection.ProjectionTimeUnit.Day;

            case "week":
                return projection.ProjectionTimeUnit.Week;
            case "month":
                return projection.ProjectionTimeUnit.Month;
        }
        return null;
    }

    public static var.VarType getVarType(String  type){

        switch(type) {
            case "int":
                return var.VarType.Int;
            case "String":
                return var.VarType.String;
            case "char":
                return var.VarType.Char;

            case "Double":
                return var.VarType.Double;
        }
        return null;

    }

    public  static Action.AggregationOperators getAggregationOp(String op)
    {
        switch(op) {
            case "==":
                return Action.AggregationOperators.Equal;
            case ">=":
                return Action.AggregationOperators.GreatEqual;
            case "<=":
                return Action.AggregationOperators.LessEqual;
            case "<":
                return Action.AggregationOperators.LessThen;
            case ">":
                return Action.AggregationOperators.GreaterThen;

        }
        return null;
    }
    public static var.Operators getVarOp(String op) {
        switch(op) {
            case "==":
                return var.Operators.Equal;
            case ">=":
                return var.Operators.GreatEqual;
            case "<=":
                return var.Operators.LessEqual;
            case "<":
                return var.Operators.LessThen;
            case ">":
                return var.Operators.GreaterThen;

        }
        return null;
    }

    public  static Action.AggregationAction getAggregationAction(String op)
    {
        switch(op) {
            case "count":
                return Action.AggregationAction.Count;
            case "avg":
                return Action.AggregationAction.Avg;
            case "sum":
                return Action.AggregationAction.Sum;
        }
        return null;
    }

    public static projection.ProjectionType convertToProjectionType(String type) {
        switch(type) {
            case "cyc":
                return projection.ProjectionType.Cyclic;
            case "monitor":
                return projection.ProjectionType.Monitor;
        }
        return null;
    }

    public static ExecuteMode convertToExecuteMode(String type) {
        switch(type) {
            case "seq":
                return ExecuteMode.Sequential;
            case "parll":
                return ExecuteMode.Parallel;
        }
        return null;
    }

    public static ActionType getActionType(String type) {
        switch(type) {
            case "measure":
                return ActionType.Measurement;
            case "callback":
                return ActionType.CallBack;
            case "ask":
                return ActionType.Question;
            case "notification":
                return ActionType.Notification;
            case "recommendation":
                return ActionType.Recommendation;
        }
        return null;
    }

}
