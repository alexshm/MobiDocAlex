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

import java.util.Calendar;
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
      public AlarmManager alramMng;
    protected PendingIntent alarmInt;
    // Intent used for binding to LoggingService

    public   Intent serviceIntent =null;
    public   Context context;
    public Messenger MessengerToMsgService=null;
   // protected  Messenger mActionTriggeredMessenger = new Messenger(new IncomingMsgHandler());

    protected boolean mIsBound=false;

    @Override
    public void onReceive(Context context, Intent intent) {

       Log.i("projections.","trigger happend!!");

        //trigget the action
       // doAction();

        ///test////
        Action ac=new Action(Action.ActionType.Question,"is this your first time?","8978",context);
        InvokeAction(ac);
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
        Second, Minute, Hour, Day, Week,Month
    }


    public projection(ProjectionType type,String ProjectionName,Context _context)
    {
        Type=type;
        calanders=new Vector<Calendar>();
        Calendar c=Calendar.getInstance();
        calanders.add(c);

        context =new ContextWrapper(_context);

        serviceIntent  = new Intent(_context, example.com.mobidoc.MsgRecieverService.class);
        IntentFilter intentFilter = new IntentFilter("trigger");


        context.registerReceiver(this, intentFilter);
        alramMng= (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

    }


    public boolean Isbound()
    {
        return mIsBound;
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




    public   void startProjection() {

        if (mIsBound) {
            Toast.makeText(context, "service allready started", Toast.LENGTH_LONG).show();
        } else {
           // Intent serviceIntent = new Intent(this, MsgRecieverService.class);

            context.bindService(serviceIntent, mconnection, Context.BIND_AUTO_CREATE);
            mIsBound = true;
            Toast.makeText(context, "service succefully started", Toast.LENGTH_LONG).show();
        }
    }


    public void InvokeAction(Action a) {

        Message msg = a.getActionToSend();

        try {
            MessengerToMsgService.send(msg);
        } catch (RemoteException e) {
            Log.e("projection.InvokeAction","error sending msg: "+msg.getData().getString("value"));
        }

    }


	public  Calendar getTimer()
	{
		return calanders.get(0);
		
	}
	
	
	public  void SetTime(int year,int month,int day,int hour,int minute,int sec)
	{
		calanders.get(0).set(year, month, day, hour, minute, sec);

	}

    public void StartProjecction_alarm()
    {

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

        Intent i=new Intent("trigger");
        //context.sendBroadcast(i,android.Manifest.permission.VIBRATE);


        alarmInt=PendingIntent.getBroadcast(context,0,i,0);

    }
	
	
	public abstract void setTimer();


/*
    @Override
    public IBinder onBind(Intent intent) {
        return  null;

    }
	*/
}
