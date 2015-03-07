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
    protected  String[]  days;
    protected  int onSpacificCount;
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
        days=null;
        this.cyclicCalendar=Calendar.getInstance();
        this.cyclicCalendar.setTime(nowT);
        this.cyclicCalendar.add(Calendar.MINUTE, 2);
        String now= sdf.format(cyclicCalendar.getTime());
        //============================== Start time===========
        StartTime=now;
        onSpacificCount=0;
        //setting the Calendar for the start time
        setStartTime(now);
       // this.addAction(new MeasurementAction("testmeasure_test","58",c));
        //repeatOnDays("1,5");
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //trigget the action
        //this.doAction();
        //get the name of the Intent
        boolean isReminder = intent.getAction().contains("_remainder");

        // we dont use alarm by days -> but we use alarm every X day/hours..
        // if days!=null -> we trigger the alaram with "repeatDays_TriggerNextDay"
        Log.i("cyclic projection -  onReceive.", "trigger action successfully");
        if(days!=null&& !isReminder) {
            alramMng.cancel(alarmInt);
            repeatDays_TriggerNextDay();

        }

        if (action!= null) {
            Log.i("cyclic projection -  onReceive.", "action not null");
           // actions.get()
            action.invoke();
           // this.InvokeAction(isReminder);

        } else {
            Log.i("projections.", "action is  nulll!!!");
        }

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
    public boolean hasReaminder()
    {
        return (remainderTime>0 || remainderCalendar!=null);
    }


    public void repeatOnDays(String listOfDays)
    {
        setAlarmTrigger();
       days=listOfDays.split(",");
        onSpacificCount=0;
        repeatDays_TriggerNextDay();
    }
    private void repeatDays_TriggerNextDay()
    {

       Calendar temp= Calendar.getInstance();
        temp.setTime(new Date());
        int today=temp.get(Calendar.DAY_OF_WEEK);
        int index=onSpacificCount %days.length;
        int nextday=Integer.parseInt(days[index]);
        int daystoadd=nextday-today;
        if(daystoadd<0)

        {
            daystoadd=(7-today)+nextday;
        }
        temp.add(Calendar.DATE,daystoadd);
        System.out.println("the new date is  is : "+ temp.getTime());

       alramMng.set(AlarmManager.RTC_WAKEUP,temp.getTimeInMillis(),alarmInt);
        onSpacificCount++;

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

        //TODO: register action to condition trigger( after the change of compositeAction

        //if( action!=null)
         //   triggerName=action.getActionName()+"_condition";

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

        // we dont use alarm by days -> but we use alarm every X day/hours..
        // if days!=null -> we trigger the alaram with "repeatDays_TriggerNextDay"
        if(days==null)
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
