package example.com.mobidoc;

import android.app.DialogFragment;

import android.app.FragmentManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;


public class MsgRecieverService extends Service {



    private static final String TAG = "LoggingService";
    public int count=0;
    // Messenger Object that receives Messages from connected clients
     Messenger mMessenger = new Messenger(new IncomingMsgHandler());

     class IncomingMsgHandler extends Handler {

        private static final int MEASURE_MSG = 4;
        private static final int QUESTION_MSG = 1;
        private static final int RECOMMENDATION_MSG = 2;
        private static final int NOTIFICATION_MSG = 3;
        private static final int REMINDER_MSG = 6;

        @Override
        public void handleMessage(Message msg) {
            String ans="";

            count++;
            switch (msg.what) {
                case (NOTIFICATION_MSG):

                    ans = "notificatin msg "+msg.getData().getString("value");

                    break;
                case (QUESTION_MSG):

                    ans = "question MSG "+msg.getData().getString("value");
                    break;
                case (RECOMMENDATION_MSG):

                    ans = "reccomendation msg "+msg.getData().getString("value");
                    break;
                case (MEASURE_MSG):
                    ans = "measure MSG "+msg.getData().getString("value");

                    break;
                case (REMINDER_MSG):
                    ans = "this is a reminder msg for : "+msg.getData().getString("value");

                    break;


                default:
                    super.handleMessage(msg);
            }


            Toast.makeText(getBaseContext(), ans+" total msg count: "+count, Toast.LENGTH_LONG).show();
            DialogFragment dialog=MainScreen.BuildDialog.newInstacce(ans);

            //TODO: NEED TO FIX SHOWING DIALOG *****
        //    dialog.show(FragmentManager.class.cast(FragmentManager.class), "question");

        }


    }
    @Override
    public IBinder onBind(Intent intent) {

        return mMessenger.getBinder();
    }
}
