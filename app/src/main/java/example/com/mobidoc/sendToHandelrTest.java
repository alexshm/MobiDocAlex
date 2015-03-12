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

    private Intent serviceIntent =null;
    private Context context;
    private Messenger MessengerToMsgService=null;

    private boolean mIsBound;

    private ServiceConnection mconnection= new ServiceConnection() {

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

    public sendToHandelrTest(Context c)
    {
        context =new ContextWrapper(c);
        mIsBound=false;
        serviceIntent  = new Intent(context, example.com.mobidoc.MsgRecieverService.class);
        startService();
    }


    public   void startService() {
        if (!mIsBound) {
            context.bindService(serviceIntent, mconnection, Context.BIND_AUTO_CREATE);
            this.mIsBound = true;

        }
    }

    public void send(Action action)
    {

            try {
                if(action!=null) {

                    Message m = action.call();
                   Log.i("sendToHandelrTest","sending: "+m.getData().getString("value"));

                    if (m != null)
                        this.MessengerToMsgService.send(m);
                }

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
