package projections;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.content.ComponentName;
import android.content.ServiceConnection;

import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;
import example.com.mobidoc.*;


public abstract class projection extends BroadcastReceiver {

    final static long SECOND=1000L;
    final static long MINUTE=60*SECOND;
    final static long HOUR=60*MINUTE;
    final static long DAY=24*HOUR;
    final static long WEEK=7*DAY;



    protected ProjectionType Type;
    protected Vector<Calendar> calanders;
    protected  String ProjectionName;
  protected long reapetTime;
    protected long remainderTime;
      public AlarmManager alramMng;
    protected PendingIntent alarmInt;
    protected PendingIntent reaminderInt;
        protected  Action action;
    // Intent used for binding to LoggingService

    public   Intent serviceIntent =null;
    public   Context context;
    public Messenger MessengerToMsgService=null;
   // protected  Messenger mActionTriggeredMessenger = new Messenger(new IncomingMsgHandler());

    protected boolean mIsBound=false;

    @Override
    public void onReceive(Context context, Intent intent) {



        //trigget the action
        //this.doAction();


        //get the name of the Intent
        boolean isReminder=intent.getAction().equals(this.ProjectionName+"_remainder");

       if(this.action!=null) {
           Log.i("projections.","trigger action successfully");

            this.InvokeAction(this.action,isReminder);

       }
       else {
           Log.i("projections.", "action is  nulll because....!!!");
           Log.i("projections.", "trigger happend!! from " + this.Type.toString() + " and the name is  : " + this.ProjectionName);
       }
    }
    protected ServiceConnection mconnection= new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MessengerToMsgService=new Messenger(service);
            mIsBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MessengerToMsgService=null;

            mIsBound=false;
        }
    };
    //===========================================

    public enum ProjectionType {
        Cyclic, Monitor, Question, Recommendation, Notification, Measurement
    }



    public enum ProjectionTimeUnit {
        Second, Minute, Hour, Day, Week,Month ,None
    }

    public  String getProjectionName()
    {
        return ProjectionName;
    }

    public void SetStartTime(int startHour,int startMinute)
    {
        calanders.get(0).set(Calendar.HOUR_OF_DAY,startHour);

        calanders.get(0).set(Calendar.MINUTE,startMinute);
    }

    protected void setAction(Action a)
    {

        Log.i("projection absctract","set action  "+a.actionName);
       // setAction(m);
        this.action=a;
        Log.i("projection absctract","this set action  is "+this.action.actionName);
    }
    public projection(ProjectionType type,String _ProjectionName,Context _context)
    {
        Type=type;
        calanders=new Vector<Calendar>();
        Calendar c=Calendar.getInstance();
        calanders.add(c);
        ProjectionName=_ProjectionName;
        context =new ContextWrapper(_context);

        serviceIntent  = new Intent(_context, example.com.mobidoc.MsgRecieverService.class);

        alramMng= (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

    }

    public abstract  void registerToTriggring();


    public boolean Isbound()
    {
        return mIsBound;
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
	
	public abstract void doAction();


    public   void StopProjection() {

        if (mIsBound) {
            mconnection = null;
            mIsBound = false;
            context.stopService(serviceIntent);
            Toast.makeText(context, "service succefully stopped", Toast.LENGTH_LONG).show();
        } else
        {
            Toast.makeText(context, "service allready stopped", Toast.LENGTH_LONG).show();
        }
    }

     //  public abstract void simulateBuild();


    public   void startProjection() {

       // simulateBuild();

        if (mIsBound) {
            Toast.makeText(this.context, "service allready started", Toast.LENGTH_LONG).show();
        } else {
           // Intent serviceIntent = new Intent(this, MsgRecieverService.class);

            this.context.bindService(this.serviceIntent, this.mconnection, Context.BIND_AUTO_CREATE);
            this.mIsBound = true;
            Toast.makeText(this.context, "service succefully started", Toast.LENGTH_LONG).show();
        }
    }


    public void InvokeAction(Action a,boolean isReminder) {

        Message msg = a.getActionToSend(isReminder);


        try {
            if(msg!=null)
                this.MessengerToMsgService.send(msg);
            else
                Log.e("projection.InvokeAction","MSG is null");
        } catch (RemoteException e) {
            Log.e("projection.InvokeAction","error sending msg: "+msg.getData().getString("value"));
        }

    }


	public  Calendar getTimer()
	{
		return this.calanders.get(0);
		
	}
	
	public boolean hasReaminder()
    {
        return (remainderTime>0 || calanders.size()>1);
    }


    //set the start time to start the Projection
    // and start the alarm from this time
    // in case the remaider Unit != NONE
    // set and  start the timer for reminder
    public void StartProjecction_alarm(String StartTime,ProjectionTimeUnit reminder_unit ,int reminder_amount)
    {
        int hour=Integer.parseInt(StartTime.split(":")[0]);
        int minute=Integer.parseInt(StartTime.split(":")[1]);

        SetStartTime(hour,minute);
        //TODO: UN COMMENT setReaminder ,  when finish implementing
        //setReaminder(reminder_unit,reminder_amount);

        if (remainderTime>0) {


            Intent i=new Intent(this.ProjectionName+"_remainder");

            reaminderInt= PendingIntent.getBroadcast(this.context, 0, i, 0);

            Calendar c=Calendar.getInstance();
            calanders.add(c);
            calanders.get(1).setTimeInMillis(calanders.get(0).getTimeInMillis()-remainderTime);
            Log.i("projection", "the normal trigger is set to : "+ calanders.get(0).get(Calendar.HOUR_OF_DAY)+":"+calanders.get(0).get(Calendar.MINUTE)+":"+calanders.get(0).get(Calendar.SECOND));
            Log.i("projection", "the remainder is set to : "+ calanders.get(1).get(Calendar.HOUR_OF_DAY)+":"+calanders.get(1).get(Calendar.MINUTE)+":"+calanders.get(1).get(Calendar.SECOND));

            alramMng.setRepeating(AlarmManager.RTC_WAKEUP, calanders.get(1).getTimeInMillis(), reapetTime , reaminderInt);
        }

        alramMng.setRepeating(AlarmManager.RTC_WAKEUP,getTimer().getTimeInMillis(),reapetTime,alarmInt);

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
                Calendar temp = getTimer();
                temp.add(Calendar.MONTH, amount);
                reapetTime = getTimer().getTimeInMillis() - temp.getTimeInMillis();
                break;
            default:
                reapetTime = 30 * SECOND;
        }

        Log.i("projection","set reapet time for : "+reapetTime);
       // Intent i=new Intent("trigger");
        //context.sendBroadcast(i,android.Manifest.permission.VIBRATE);


       // alarmInt=PendingIntent.getBroadcast(context,0,i,0);

    }

    protected abstract void  setAlarmTrigger();

	



/*
public abstract void setTimer();
    @Override
    public IBinder onBind(Intent intent) {
        return  null;

    }
	*/
}
