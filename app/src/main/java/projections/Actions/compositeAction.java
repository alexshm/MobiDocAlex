package projections.Actions;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import projections.ActionsExecutor.*;

import projections.Utils;


public class compositeAction  {

    public ArrayList<Action> actionsCollection;
    private Context context;



   private actionExecutor ex;

    public compositeAction(Context c, Utils.ExecuteMode mode) {
        actionsCollection= new ArrayList<Action>( );
        context =new ContextWrapper(c);
        if (mode.equals(Utils.ExecuteMode.Sequential))
            ex=new SerialExecutor(c);
        else
            ex=new ParallelExecuter(c);


    }

    public void setExecuteMode(Utils.ExecuteMode executeMode)
    {
        if (executeMode.equals(Utils.ExecuteMode.Sequential))
            ex=new SerialExecutor(context);
        else
            ex=new ParallelExecuter(context);
    }
    public void invoke() {

        Runnable r = null;
        this.ex.execute(r);


    }

    public void addAction(Action action)
    {

        actionsCollection.add(action);
        this.ex.addAction(action);
    }
}
