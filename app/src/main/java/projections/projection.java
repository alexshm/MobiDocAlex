package projections;

import android.content.BroadcastReceiver;
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

import projections.Actions.Action;


public abstract class projection extends BroadcastReceiver {


    protected ProjectionType Type;

    protected  String ProjectionName;
   // protected PendingIntent alarmInt;
    protected  Action action;

    // Intent used for binding to LoggingService
    public   Intent serviceIntent =null;
    public   Context context;
    public Messenger MessengerToMsgService=null;

    protected  boolean hasAlarm;

    protected boolean mIsBound=false;

    public enum ProjectionType {
        Cyclic, Monitor, Question, Recommendation, Notification, Measurement
    }

    public enum ProjectionTimeUnit {
        Second, Minute, Hour, Day, Week,Month ,None
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

    public  String getProjectionName()
    {
        return ProjectionName;
    }

    protected void setAction(Action a)
    {

       // setAction(m);
        action=a;
        Log.i("projection "," set action  is "+action.getActionName());
    }
    public projection(ProjectionType type,String _ProjectionName,Context _context)
    {
        Type=type;
        ProjectionName=_ProjectionName;
        context =new ContextWrapper(_context);
        serviceIntent  = new Intent(_context, example.com.mobidoc.MsgRecieverService.class);

    }

    public abstract  void registerToTriggring();


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
    public void startAlarm()
    {
        return;
    }



    public   void startProjection() {
        initProjection();
        if (mIsBound) {
            Toast.makeText(this.context, "service allready started", Toast.LENGTH_LONG).show();
        } else {
           // Intent serviceIntent = new Intent(this, MsgRecieverService.class);

            this.context.bindService(this.serviceIntent, this.mconnection, Context.BIND_AUTO_CREATE);
            this.mIsBound = true;
            Toast.makeText(this.context, "service succefully started", Toast.LENGTH_LONG).show();
        }

        if(hasAlarm)
            this.startAlarm();


    }

    public  abstract void initProjection();

    public void InvokeAction(Action a,boolean isReminder) {

            System.out.println(" invoke action : "+a.getConcept()+ "is remider: "+isReminder);
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




}
