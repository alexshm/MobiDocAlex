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
import java.util.Vector;

import projections.MeasurementAction;
import projections.projection.ProjectionTimeUnit;

public abstract class CyclicProjectionAbstract extends projection {

    final static long SECOND=1000L;
    final static long MINUTE=60*SECOND;
    final static long HOUR=60*MINUTE;
    final static long DAY=24*HOUR;
    final static long WEEK=7*DAY;

    protected Vector<Calendar> calanders;
    protected long remainderTime;
    public AlarmManager alramMng;
    protected PendingIntent alarmInt;
    protected PendingIntent reaminderInt;
    protected  long reapetTime;
    protected  Calendar remainderC;
    public CyclicProjectionAbstract(String projectionName, Context c) {
        super(ProjectionType.Cyclic, projectionName, c);

        calanders=new Vector<Calendar>();
        Calendar clndr=Calendar.getInstance();
        calanders.add(clndr);
         remainderC=Calendar.getInstance();
        calanders.add(remainderC);
        alramMng= (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);

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
            Log.i("projections.", "action is  nulll because....!!!");
            Log.i("projections.", "trigger happend!! from " + this.Type.toString() + " and the name is  : " + this.ProjectionName);
        }
    }
    public  void setFrequency(ProjectionTimeUnit unit, int amout)
    {
        SetDoActionEvery(unit, amout);
    }

    public boolean hasReaminder()
    {
        return (remainderTime>0 || calanders.size()>1);
    }

    public  void SetDoActionEvery(ProjectionTimeUnit timeunit,int amount) {

        reapetTime=0;

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
                Calendar temp = calanders.get(0);
                temp.add(Calendar.MONTH, amount);
                reapetTime = calanders.get(0).getTimeInMillis() - temp.getTimeInMillis();
                break;
            default:
                reapetTime = 30 * SECOND;
        }

        Log.i("projection","set reapet time for : "+reapetTime);


        //set what will trigger the alarm
        setAlarmTrigger();

    }

    // what action to do
    public abstract void makeTestCyclic();

     public void startCyclic(ProjectionTimeUnit unit,int amout,ProjectionTimeUnit reamiderUnit,int reamiderAmount) {

         // TODO:  un ccomment this when projecction is ready
         SetDoActionEvery(unit,amout);
         setReaminder(reamiderUnit,reamiderAmount);
         makeTestCyclic();


        this.registerToTriggring();

         //============================== Start time===========
         Date nowT=new Date();
         SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

         Calendar c=Calendar.getInstance();
         c.setTime(nowT);
         c.add(Calendar.MINUTE, 4);
         String now= sdf.format(c.getTime());
         //============================== Start time===========

        String startTime=now;
         int hour=Integer.parseInt(startTime.split(":")[0]);
         int minute=Integer.parseInt(startTime.split(":")[1]);


         SetStartTime(hour,minute);
         super.startProjection();
        StartProjecction_alarm(startTime,reamiderUnit,reamiderAmount);


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
            triggerName=action.actionName+"_condition";

        IntentFilter TriggerConditionIntentFilter = new IntentFilter(triggerName);

        context.registerReceiver(this, TriggerConditionIntentFilter);
        Log.i("register to  Trigger for Condition","register  to "+triggerName);
    }

    //set the start time to start the Projection
    // and start the alarm from this time
    // in case the remaider Unit != NONE
    // set and  start the timer for reminder
    public void StartProjecction_alarm(String StartTime,ProjectionTimeUnit reminder_unit ,int reminder_amount)
    {

        //TODO: UN COMMENT setReaminder ,  when finish implementing
        //setReaminder(reminder_unit,reminder_amount);

        if (remainderTime>0) {

            setRemainderTrigger();

            Calendar c1=Calendar.getInstance();

            //c1.set(Calendar.HOUR_OF_DAY,calanders.get(0).get(Calendar.HOUR_OF_DAY));
           // c1.set(Calendar.MINUTE,calanders.get(0).get(Calendar.MINUTE)-1);
            c1.setTimeInMillis(calanders.get(0).getTimeInMillis()- remainderTime);
            calanders.add(c1);


            Log.i("projection", "the normal trigger is set to : "+ calanders.get(0).get(Calendar.HOUR_OF_DAY)+":"+calanders.get(0).get(Calendar.MINUTE)+":"+calanders.get(0).get(Calendar.SECOND));
            Log.i("projection", "the remainder is set to : "+ calanders.get(1).get(Calendar.HOUR_OF_DAY)+":"+calanders.get(1).get(Calendar.MINUTE)+":"+calanders.get(1).get(Calendar.SECOND));

            alramMng.setRepeating(AlarmManager.RTC_WAKEUP,  calanders.get(1).getTimeInMillis(), reapetTime, reaminderInt);
        }

        alramMng.setRepeating(AlarmManager.RTC_WAKEUP,calanders.get(0).getTimeInMillis(),reapetTime,alarmInt);

    }

    public void SetStartTime(int startHour,int startMinute)
    {
        calanders.get(0).set(Calendar.HOUR_OF_DAY,startHour);

        calanders.get(0).set(Calendar.MINUTE,startMinute);
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

            default:
                remainderTime = 30 * SECOND;
        }
    }

    //set what will trigger the alarm
    protected void setAlarmTrigger() {
        Intent i=new Intent(ProjectionName);

        //context.sendBroadcast(i,android.Manifest.permission.VIBRATE);

        alarmInt= PendingIntent.getBroadcast(context, 0, i, 0);
    }

    //set what will trigger the remainder alarm
    protected void setRemainderTrigger() {
        Intent i = new Intent(ProjectionName + "_remainder");
        reaminderInt = PendingIntent.getBroadcast(context, 0, i, 0);

    }
}
