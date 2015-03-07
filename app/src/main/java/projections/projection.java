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

import java.util.Vector;

import projections.Actions.Action;
import projections.Actions.compositeAction;


public abstract class projection extends BroadcastReceiver {


    protected ProjectionType Type;

    protected  String ProjectionName;

    public   Context context;


    protected  boolean hasAlarm;
    protected compositeAction action;
    protected boolean mIsBound=false;
    protected Utils.ExecuteMode mode;
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


    public void  setExectuionMode(Utils.ExecuteMode executeMode)
    {
        mode=executeMode;
        action.setExecuteMode(mode);
    }
    public void addAction(Action a)
    {
        //TODO:
        action.addAction(a);
        // setAction(m);

        Log.i("projection "," add action ");
    }


    public projection(ProjectionType type,String _ProjectionName,Context _context)
    {
        Type=type;
        ProjectionName=_ProjectionName;
        context =new ContextWrapper(_context);
        action=new compositeAction(context, Utils.ExecuteMode.Sequential);
        mode= Utils.ExecuteMode.Sequential;

    }

    public abstract  void registerToTriggring();


    public boolean Isbound()
    {
        return mIsBound;
    }

	
	public abstract void doAction();


    public   void StopProjection() {



    }
    public void startAlarm()
    {
        return;
    }



    public   void startProjection() {
        initProjection();
        //TODO : start the compositeActin serivice

        if(hasAlarm)
            this.startAlarm();


    }

    public  abstract void initProjection();

    public void InvokeAction(boolean isReminder) {

        }

}
