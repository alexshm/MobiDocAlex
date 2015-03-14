package projections.ActionsExecutor;

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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import projections.Actions.Action;
import projections.Utils;

/**
 * Created by Moshe on 3/4/2015.
 */
public abstract class actionExecutor implements Executor{

    protected ArrayList<Action> tasks ;
    protected List<Future<Message>> msgsrstlt;
    protected boolean mIsBound=false;
    protected ExecutorService pool;

    public Intent serviceIntent;
    public Context context;
    public Messenger MessengerToMsgService;



    public actionExecutor(Context c)
    {
        context =c;
        serviceIntent  = new Intent(context, example.com.mobidoc.MsgRecieverService.class);
    }

    public ServiceConnection mconnection= new ServiceConnection() {

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
    public   void startService() {
        if (mIsBound) {
            Toast.makeText(context, "action executer  allready started", Toast.LENGTH_SHORT).show();
        } else {

            context.bindService(serviceIntent, mconnection, Context.BIND_AUTO_CREATE);
           mIsBound = true;
            Toast.makeText(context, "action executer started", Toast.LENGTH_LONG).show();
        }
    }

    public   void StopService() {

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


     public List<Future<Message>> getMessagesList()
     {
         return msgsrstlt;
     }
    public void clean()
    {
        msgsrstlt.clear();
    }

    public  void addAction(Action a)
    {
        Log.i(" actionExecutier" ,"adding action - do nothing");
    }
    public ArrayList getCollection()
    {
        return  tasks;
    }
    public void send()
    {
        Log.i(" actionExecutier" ,"preparing to send msgs");
        for (Future msg : msgsrstlt) {
            try {
                Message m = (Message) msg.get();
                System.out.println(" sending msg : "+m.getData().getString("value"));

                if (m != null)
                    MessengerToMsgService.send(m);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        this.clean();
    }

    @Override
      public void execute(Runnable command) {
        Log.i(" actionExecutier" ,"do nothing");

    }

}
