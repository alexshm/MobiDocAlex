package projections;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Vibrator;
import android.widget.Toast;


public class ProjectionActionTriggerService extends Service {

    private static final String TAG = "ProjectionMsgRecieverService";

    // Messenger Object that receives Messages from connected clients
    Messenger mMessenger = new Messenger(new IncomingMsgHandler());



    class IncomingMsgHandler extends Handler {

        private static final int ACTION_TRIGGERED = 1;


        @Override
        public void handleMessage(Message msg) {
            String ans="";


            switch (msg.what) {
                case (ACTION_TRIGGERED):

                    ans = msg.getData().getString("ans");

                    break;


                default:
                    super.handleMessage(msg);
            }





        }


    }







    @Override
    public IBinder onBind(Intent intent) {

        return mMessenger.getBinder();
    }
}


