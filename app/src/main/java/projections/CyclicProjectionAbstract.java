package projections;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class CyclicProjectionAbstract extends projection {

    final static long SECOND=1000L;
    final static long MINUTE=60*SECOND;
    final static long HOUR=60*MINUTE;
    final static long DAY=24*HOUR;
    final static long WEEK=7*DAY;

    public Calendar cyclicCalendar;
    public Calendar remainderCalendar;

    public long remainderTime;
    private AlarmManager alramMng;
    public AlarmManager repeatmMng;
    public PendingIntent alarmInt;
    public PendingIntent reaminderInt;
    public  long reapetTime;
    protected Date reminderTime;
    protected Date cycTime;

    Context c1;
    protected  String[]  days;
    protected  int onSpacificCount;
    public  String StartTime;
    public ProjectionTimeUnit reminderUnit;
    public int reminder_amount;

    public CyclicProjectionAbstract(String projectionName,String id, Context c ) {
        super(ProjectionType.Cyclic, projectionName, id,c);
        ProjectionName=projectionName;
        this.alramMng= (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        hasAlarm=true;
        StartTime="";
        remainderTime=0;
        reminderUnit=null;
        reminder_amount=0;
        remainderCalendar=null;
        cyclicCalendar=Calendar.getInstance();
        days=null;
        onSpacificCount=0;
        cycTime=new Date();
        reminderTime=new Date();

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("cyclicProj -onReceive", "recive data :proj name : "+this.ProjectionName);
        //get the name of the Intent
        boolean isReminder = intent.getAction().contains("_remainder");

        Log.i("cyclicProj -onReceive", "has reminder : "+isReminder);
        if(isReminder)
        {
            reminderTime.setTime(reminderTime.getTime()+reapetTime);
            Log.i("cyclic on recieve ","the new reminder time is : "+reminderTime);
           // alramMng.cancel(reaminderInt);
            //   alramMng.getNextAlarmClock().getShowIntent()
            alramMng.set(AlarmManager.RTC_WAKEUP,cycTime.getTime(),alarmInt);
        }
        else
        {
            cycTime.setTime(cycTime.getTime()+reapetTime);
            Log.i("cyclic on recieve ","the new cyclic  time is : "+cycTime);
          //  alramMng.cancel(alarmInt);

            alramMng.set(AlarmManager.RTC_WAKEUP,reminderTime.getTime(),reaminderInt);

        }


        boolean isCyc = intent.getAction().contains("_cyc");

        if(isCyc||isReminder) {
            // if we dont use alarm by days -> but we use alarm every X day/hours..
            // if days!=null -> we trigger the alaram with "repeatDays_TriggerNextDay"
            Log.i("cyclicProj -onReceive", "trigger action successfully");
            if (days != null && !isReminder) {

                alramMng.cancel(alarmInt);
                repeatDays_TriggerNextDay();

            }

            if (action != null) {
                Log.i("cyclicproj("+getProjectionId()+")","onReceive - action not null");
                action.invoke(isReminder);


            } else {
                Log.e("projections.", "action is  nulll!!!");
            }
        }
        else
        {
            //receive ans save the incoming data and also check the
            // condition if it happend or not
            receiveData(intent);
        }

    }
    public void setStartTime(String startTime)
    {
        //===========================================
        int hour=Integer.parseInt(startTime.split(":")[0]);
        int minute=Integer.parseInt(startTime.split(":")[1]);
        this.cyclicCalendar.set(Calendar.HOUR_OF_DAY, hour);
        this.cyclicCalendar.set(Calendar.MINUTE,minute);

        this.cycTime=new Date(cyclicCalendar.getTime().getTime());
        Log.i("cyclic projection", "the normal calander is set to : " + cyclicCalendar.get(Calendar.HOUR_OF_DAY) + ":" + cyclicCalendar.get(Calendar.MINUTE) + ":" + cyclicCalendar.get(Calendar.SECOND));


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
    public  void setFrequency(String timeunit,int amount) {

        reapetTime=0;
        projection.ProjectionTimeUnit repUnit=Utils.getTimeUnit(timeunit);
        switch (repUnit) {

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

        //set what will trigger the alarm
        setAlarmTrigger();


    }


    @Override
    public void initProjection() {

        this.registerToTriggring();



    }

    @Override
    public void registerToTriggring()
    {
        IntentFilter intentFilter = new IntentFilter(ProjectionName+"_cyc");


        //register to triggring timer events
        Log.i("register to trigger","register  to "+ProjectionName+"_cyc");
        context.registerReceiver(this, intentFilter);
        //register to remainder event
        IntentFilter RemainderintentFilter = new IntentFilter(this.ProjectionName+"_remainder");

        context.registerReceiver(CyclicProjectionAbstract.this, RemainderintentFilter);


        //register to satisfy condition events like : 2 abnormal in past week
        String triggerName="condition";

        //register all the concepts defined in the actions
        // appears in the CompositeAction


        Vector<IntentFilter> intentesToMonitor=new Vector<IntentFilter>();
        Vector<String> conceptsToMonitor=action.getAllConcepts();
        Log.i("Cyclic Projection","the size of the concepts  is : "+conceptsToMonitor.size());
        for (int i=0;i<conceptsToMonitor.size();i++)
        {
            IntentFilter in = new IntentFilter(conceptsToMonitor.get(i));
            Log.i("Cyclic Projection","registering to concept: "+conceptsToMonitor.get(i));
            intentesToMonitor.add(in);
            context.registerReceiver(this, in);
        }

        IntentFilter TriggerConditionIntentFilter = new IntentFilter(triggerName);

        context.registerReceiver(this, TriggerConditionIntentFilter);
        Log.i("Cyclic Projection","register to  Triggering-register  to "+triggerName);
    }

    //set the start time to start the Projection
    // and start the alarm from this time
    // in case the remaider Unit != NONE
    // set and  start the timer for reminder
    @Override
    public void startAlarm()
    {
        // if there is no reminder run the alarm by setRepeating
        if(!hasReaminder())
        {
            alramMng.setRepeating(AlarmManager.RTC_WAKEUP,cyclicCalendar.getTimeInMillis(),reapetTime,alarmInt);

        }
        else
        {
            //  if we have a reminder -> first set the alarm
            Date s=remainderCalendar.getTime();
            //  Log.i("projection", "the normal trigger is set to : " + cyclicCalendar.get(Calendar.HOUR_OF_DAY) + ":" + cyclicCalendar.get(Calendar.MINUTE) + ":" + cyclicCalendar.get(Calendar.SECOND));
            Log.i("CyclicProj("+getProjectionId()+")","-startAlarm -the remainder is set to : "+ remainderCalendar.getTime()) ;

            alramMng.set(AlarmManager.RTC_WAKEUP, remainderCalendar.getTimeInMillis(), reaminderInt);
            reminderTime=new Date(remainderCalendar.getTimeInMillis());
            cycTime=new Date(cyclicCalendar.getTimeInMillis());

            //  alramMng.setRepeating(AlarmManager.RTC_WAKEUP, remainderCalendar.getTimeInMillis(), reapetTime, reaminderInt);

        }



        // TODO: fix the repet withj dayys
        // we dont use alarm by days -> but we use alarm every X day/hours..
        // if days!=null -> we trigger the alaram with "repeatDays_TriggerNextDay"
        // if(days==null)
        //   alramMng.setRepeating(AlarmManager.RTC_WAKEUP,cyclicCalendar.getTimeInMillis(),reapetTime,alarmInt);




    }
    public void setReaminder(String remainderUnit, int amount)
    {

        projection.ProjectionTimeUnit remUnit=Utils.getTimeUnit(remainderUnit);

        if(amount!=0) {
            setCyclicReaminder(remUnit, amount);
        }
    }

    private  void setCyclicReaminder(ProjectionTimeUnit unit, int amount)
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
            remainderCalendar.setTime(cyclicCalendar.getTime());

            remainderCalendar.setTimeInMillis(cyclicCalendar.getTimeInMillis()-remainderTime);
            Log.i("set reminder", "set the reminder for "+remainderCalendar.getTime());
            setRemainderTrigger();
            // remainderCalendar.setTimeInMillis(cyclicCalendar.getTimeInMillis()- remainderTime);


        }

    }

    //set what will trigger the alarm
    public void setAlarmTrigger() {

        Intent i=new Intent(this.ProjectionName+"_cyc");


        alarmInt= PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_CANCEL_CURRENT);

    }


    //set what will trigger the remainder alarm
    public void setRemainderTrigger() {


        //int numIntent=Utils.getIntentCounter();
        Intent i2 = new Intent(this.ProjectionName + "_remainder");

        Log.i("cyclic projection ","setRemainderTrigger : "+this.ProjectionName + "_remainder");

        reaminderInt = PendingIntent.getBroadcast(context, 35, i2, PendingIntent.FLAG_CANCEL_CURRENT);


    }


}