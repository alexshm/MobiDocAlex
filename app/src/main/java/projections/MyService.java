package projections;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class MyService extends Service {


    @Override
    public IBinder onBind(Intent intent) {

        return messenger.getBinder();
    }

    class IncomingMsgHandler extends Handler {

        private static final int MEASURE_MSG = 1;
        private static final int QUESTION_MSG = 2;
        private static final int RECOMMENDATION_MSG = 3;
        private static final int NOTIFICATION_MSG = 4;


        @Override
        public void handleMessage(Message msg) {
            String ans = "";

           // System.out.println("the msg arrriives" + msg.getData().getString("value"));
            switch (msg.what) {
                case (MEASURE_MSG):

                    Toast.makeText(getBaseContext(),"HELLO FROM SERVICE",Toast.LENGTH_LONG).show();
                    Bundle bun=msg.getData();
                    String msgstr=bun.getString("value");
                    Toast.makeText(getBaseContext(),"msg recived : "+msgstr,Toast.LENGTH_LONG).show();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }

    Messenger messenger = new Messenger(new IncomingMsgHandler());
}

