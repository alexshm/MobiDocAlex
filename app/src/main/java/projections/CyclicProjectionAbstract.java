package projections;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import projections.Actions.MeasurementAction;

public class CyclicProjectionAbstract extends projection {

    final static long SECOND=1000L;
    final static long MINUTE=60*SECOND;
    final static long HOUR=60*MINUTE;
    final static long DAY=24*HOUR;
    final static long WEEK=7*DAY;

    public Calendar cyclicCalendar;
    public Calendar remainderCalendar;

    public long remainderTime;
    public AlarmManager alramMng;
    public AlarmManager repeatmMng;
    public PendingIntent alarmInt;
    public PendingIntent reaminderInt;
    public  long reapetTime;
    protected  Date  normal;
    protected Date remainder;


    public  String StartTime;
    public ProjectionTimeUnit reminderUnit;
    public int reminder_amount;

    public CyclicProjectionAbstract(String projectionName, Context c,String startTime) {
        super(ProjectionType.Cyclic, projectionName, c);

        alramMng= (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        repeatmMng= (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        hasAlarm=true;
        StartTime=startTime;
        reminderUnit=null;
        reminder_amount=0;
        remainderCalendar=null;
        cyclicCalendar=null;
        //============================== Start time===========
        Date nowT=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        this.cyclicCalendar=Calendar.getInstance();
        this.cyclicCalendar.setTime(nowT);
        this.cyclicCalendar.add(Calendar.MINUTE, 5);
        String now= sdf.format(cyclicCalendar.getTime());
        //============================== Start time===========
        StartTime=now;

        //setting the Calendar for the start time
        setStartTime(now);
        this.setAction(new MeasurementAction("testmeasure_test","58",c));
    }
    public void setStartTime(String startTime)
    {
        //===========================================
        int hour=Integer.parseInt(StartTime.split(":")[0]);
        int minute=Integer.parseInt(StartTime.split(":")[1]);
        this.cyclicCalendar.set(Calendar.HOUR_OF_DAY, hour);
        this.cyclicCalendar.set(Calendar.MINUTE,minute);
        Log.i("cyclic projection", "the normal calander is set to : " + cyclicCalendar.get(Calendar.HOUR_OF_DAY) + ":" + cyclicCalendar.get(Calendar.MINUTE) + ":" + cyclicCalendar.get(Calendar.SECOND));

        this.normal=cyclicCalendar.getTime();
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        //trigget the action
        //this.doAction();
        //get the name of the Intent
        boolean isReminder = intent.getAction().contains("_remainder");

        if (action != null) {
            Log.i("projections.", "trigger action successfully");

            this.InvokeAction(action, isReminder);

        } else {
            Log.i("projections.", "action is  nulll!!!");
        }
    }

    public boolean hasReaminder()
    {
        return (remainderTime>0 || remainderCalendar!=null);
    }



    public  void setFrequency(ProjectionTimeUnit timeunit,int amount) {

        reapetTime=0;
        Log.i("setFrequency","the amount is : "+amount);
        switch (timeunit) {

            case Second:
                reapetTime = amount * SECOND;
                break;
            case Minute:
                reapetTime = amount * MINUTE;
                break;
            case Hour:
                reapetTime = amount * HOUR;
            case Day:
                reapetTime = DAY * amount;
                break;
            case Week:
                reapetTime = WEEK * amount;
                break;
            case Month:
                //TODO: month calculation
                reapetTime=500;
                break;

        }

        Log.i("projection","set reapet time for : "+reapetTime);

        //TODO: change and fix the time start - the location where it happend

        //set what will trigger the alarm
        setAlarmTrigger();

    }

    // what action to do
    public  void makeTestCyclic(){

    }

    @Override
     public void initProjection() {

        makeTestCyclic();

        this.registerToTriggring();



      }

    @Override
    public void registerToTriggring()
    {
        IntentFilter intentFilter = new IntentFilter(ProjectionName);

        //register to triggring timer events
        Log.i("register to trigger","register  to "+ProjectionName);
        context.registerReceiver(this, intentFilter);

        //register to remainder event
        IntentFilter RemainderintentFilter = new IntentFilter(ProjectionName+"_remainder");

        context.registerReceiver(this, RemainderintentFilter);

        //register to satisfy condition events like : 2 abnormal in past week
        String triggerName="condition";
        if( action!=null)
            triggerName=action.getActionName()+"_condition";

        IntentFilter TriggerConditionIntentFilter = new IntentFilter(triggerName);

        context.registerReceiver(this, TriggerConditionIntentFilter);
        Log.i("register to  Trigger for Condition","register  to "+triggerName);
    }

    @Override
    public void doAction() {

    }

    //set the start time to start the Projection
    // and start the alarm from this time
    // in case the remaider Unit != NONE
    // set and  start the timer for reminder
    @Override
    public void startAlarm()
    {

        //TODO: UN COMMENT setReaminder ,  when finish implementing
        //setReaminder(reminder_unit,reminder_amount);

        Date s=cyclicCalendar.getTime();
        alramMng.setRepeating(AlarmManager.RTC_WAKEUP,normal.getTime(),reapetTime,alarmInt);

        if (remainderTime>0) {


            setRemainderTrigger();
          //  Log.i("projection", "the normal trigger is set to : " + cyclicCalendar.get(Calendar.HOUR_OF_DAY) + ":" + cyclicCalendar.get(Calendar.MINUTE) + ":" + cyclicCalendar.get(Calendar.SECOND));
            Log.i("projection", "the remainder is set to : "+ remainder) ;

            repeatmMng.setRepeating(AlarmManager.RTC_WAKEUP,  remainder.getTime(), reapetTime, reaminderInt);

        }



    }


    public  void setReaminder(ProjectionTimeUnit unit, int amount)
    {
        remainderTime=0;
        switch (unit) {

            case Second:
                remainderTime = amount * SECOND;
                break;
            case Minute:
                remainderTime = amount * MINUTE;
                break;
            case Hour:
                remainderTime = amount * HOUR;
            case Day:
                remainderTime = DAY * amount;
                break;
            case None:
                remainderTime = 0;
                break;


        }
        reminderUnit=unit;
        reminder_amount=amount;
        if(cyclicCalendar!=null)
        {
            remainderCalendar=Calendar.getInstance();
            remainder=new Date(normal.getTime()-remainderTime);
           // remainderCalendar.setTimeInMillis(cyclicCalendar.getTimeInMillis()- remainderTime);
            Log.i("set reminder", "set the reminder for "+remainder);
            Log.i("set reminder", "the reminder is :  "+remainderTime);
        }

    }

    //set what will trigger the alarm
    public void setAlarmTrigger() {
        Intent i=new Intent(this.ProjectionName);

        //context.sendBroadcast(i,android.Manifest.permission.VIBRATE);

        alarmInt= PendingIntent.getBroadcast(context, 0, i, 0);
    }
    //set what will trigger the remainder alarm
    public void setRemainderTrigger() {
        Intent i2 = new Intent(this.ProjectionName + "_remainder");
        reaminderInt = PendingIntent.getBroadcast(context, 1, i2, 0);

    }
}
