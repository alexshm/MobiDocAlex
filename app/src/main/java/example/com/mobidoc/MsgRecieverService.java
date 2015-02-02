package example.com.mobidoc;

import android.app.DialogFragment;

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

    // Messenger Object that receives Messages from connected clients
     Messenger mMessenger = new Messenger(new IncomingMsgHandler());



     class IncomingMsgHandler extends Handler {

        private static final int MEASURE_MSG = 1;
        private static final int QUESTION_MSG = 2;
        private static final int RECOMMENDATION_MSG = 3;
        private static final int NOTIFICATION_MSG = 4;


        @Override
        public void handleMessage(Message msg) {
            String ans="";


            switch (msg.what) {
                case (MEASURE_MSG):

                    ans = "measureMSG "+msg.getData().getString("value");

                    break;
                case (QUESTION_MSG):

                    ans = "question MSG "+msg.getData().getString("value");
                    break;
                case (RECOMMENDATION_MSG):

                    ans = "reccomendation msg "+msg.getData().getString("value");
                    break;
                case (NOTIFICATION_MSG):

                    ans = "notificatin msg "+msg.getData().getString("value");
                    break;

                default:
                    super.handleMessage(msg);
            }


            Toast.makeText(getBaseContext(), "msg recived in service : " + ans, Toast.LENGTH_LONG).show();
            DialogFragment dialog=MainScreen.BuildDialog.newInstacce(ans);



        }


    }







    @Override
    public IBinder onBind(Intent intent) {

        return mMessenger.getBinder();
    }
}
