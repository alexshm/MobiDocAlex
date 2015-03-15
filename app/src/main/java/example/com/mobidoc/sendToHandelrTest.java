package example.com.mobidoc;

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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import projections.Actions.Action;

/**
 * Created by Moshe on 3/12/2015.
 */
public class sendToHandelrTest {

    public Intent serviceIntent =null;
    public Context context;
    public  Messenger MessengerToMsgService;

    public sendToHandelrTest(Context c)
    {
        context =new ContextWrapper(c);
        mIsBound=false;
        serviceIntent  = new Intent(c,example.com.mobidoc.MsgRecieverService.class);


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
    public boolean mIsBound;






    public   void startService() {
        if (mIsBound) {
           Log.i("sendToHandelrTest", "service allready started");
        } else {

            context.bindService(serviceIntent, this.mconnection, Context.BIND_AUTO_CREATE);
            mIsBound = true;
            Toast.makeText(context, "service succefully started", Toast.LENGTH_LONG).show();
        }

        }


    public void send(Action action)
    {
        startService();
            try {
                    Message m = action.call();
                   Log.i("sendToHandelrTest","sending: "+m.getData().getString("value"));

                    if (m != null)
                        MessengerToMsgService.send(m);


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    }
