package projections;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

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


public abstract class projection extends Service {

    final static long SECOND=1000L;
    final static long MINUTE=60*SECOND;
    final static long HOUR=60*MINUTE;
    final static long DAY=24*HOUR;
    final static long WEEK=7*DAY;



    protected ProjectionType Type;
    protected Vector<Calendar> calanders;
    protected  String ProjectionName;

      public AlarmManager alramMng;

    // Intent used for binding to LoggingService

    public   Intent serviceIntent =null;
    public   Context context;
    public Messenger MessengerToMsgService=null;
    protected boolean mIsBound=false;

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
            stopService(serviceIntent);
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

        alramMng= (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);



    }
    public  void SetDoActionEvery(ProjectionTimeUnit timeunit,int amount) {

        long reapetTime=0;

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



            //set the repating time
            alramMng.setRepeating(AlarmManager.RTC_WAKEUP,getTimer().getTimeInMillis(),reapetTime,null);


    }
	
	
	public abstract void setTimer();
	


    @Override
    public IBinder onBind(Intent intent) {
        return  null;

    }
	
}
