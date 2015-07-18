package projections;


import android.os.Build;
import android.util.Log;
import android.view.View;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.os.Build;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static enum UserPreferences {
        FastingTime,
       BreakFastTime,LaunchTime,DinnerTime,
        FastingAlarm,BreakFastAlarm,LaunchAlarm,DinnerAlarm,
        OnceWeekPreferredDay,DailyTime,DailyAlarm,
        TwiceWeekPreferredDays,TwiceWeekTime
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
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);


    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < 17) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
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
    public static UserPreferences getUserPreferenceByName(String preferenceName) {

        switch(preferenceName) {
            case "breakfastTime":
                return UserPreferences.BreakFastTime;
            case "launchTime":
                return UserPreferences.LaunchTime;
            case "dinnerTime":
                return UserPreferences.DinnerTime;
            case "breakfastReminder":
                return UserPreferences.BreakFastAlarm;
            case "launchReminder":
                return UserPreferences.LaunchAlarm;
            case "dinnerReminder":
                return UserPreferences.DinnerAlarm;
            case "OnceWeek":
                return UserPreferences.OnceWeekPreferredDay;
            case "TwiceWeek":
                return UserPreferences.TwiceWeekPreferredDays;
            case "dailyTime":
                return UserPreferences.DailyTime;
            case "dailyReminder" :
                return UserPreferences.DailyAlarm;
            case "TwiceWeekTime" :
                return UserPreferences.TwiceWeekTime;


        }
        return null;
    }
    public static var.VarType getVarType(String  type){

        switch(type) {
            case "int":
                return var.VarType.Int;
            case "string":
                return var.VarType.String;
            case "char":
                return var.VarType.Char;

            case "Double":
                return var.VarType.Double;
        }
        return null;

    }

    public static String ConvertHexToString(String hexInput) {


        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexInput.length(); i += 2)
        {
            String str = hexInput.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        String ans= output.toString();
        Log.i("Utils","the parsed string is  : "+ans);

        return ans;
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

    public static Action.ActionType getActionType(String type) {
        switch(type) {
            case "measure":
                return Action.ActionType.Measurement;
            case "callback":
                return Action.ActionType.CallBack;
            case "ask":
                return Action.ActionType.Question;
            case "notification":
                return Action.ActionType.Notification;
            case "recommendation":
                return Action.ActionType.Recommendation;
        }
        return null;
    }

}
