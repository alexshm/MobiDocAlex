package example.com.mobidoc;// Object implementing Service Connection callbacks

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

public class ServiceConnectionObj implements ServiceConnection {


    protected Messenger messengerToService=null;
    protected boolean isBound=false;
    public ServiceConnectionObj()
    {

    }

    public boolean IsBound()
    {
        return isBound;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {


        // Messenger object connected to the LoggingService

        messengerToService = new Messenger(service);

        isBound = true;

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

        messengerToService=null;
        isBound=false;

    }
}
