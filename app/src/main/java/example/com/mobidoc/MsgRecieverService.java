package example.com.mobidoc;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import java.util.Random;


public class MsgRecieverService extends Service {


    public int count=0;
    // Messenger Object that receives Messages from connected clients
     IncomingMsgHandler incomingMsgHandler = new IncomingMsgHandler();
     Messenger mMessenger = new Messenger(new IncomingMsgHandler());



     class IncomingMsgHandler extends Handler {

        private static final int MEASURE_MSG = 4;
        private static final int QUESTION_MSG = 1;
        private static final int RECOMMENDATION_MSG = 2;
        private static final int NOTIFICATION_MSG = 3;
        private static final int REMINDER_MSG = 6;
        private static final int CALLBACK_MSG=5;

        @Override
        public void handleMessage(Message msg) {
            String ans="";

            count++;
            switch (msg.what) {
                case (NOTIFICATION_MSG):

                    ans = "notificatin msg "+msg.getData().getString("value");

                    break;
                case (QUESTION_MSG):

                    ans = "question msg "+msg.getData().getString("question");
                    String yesAns=msg.getData().getString("yesVal");
                    String noAns=msg.getData().getString("noVal");
                    break;
                case (RECOMMENDATION_MSG):

                    ans = "reccomendation msg "+msg.getData().getString("value");
                    String acceptConcept=msg.getData().getString("accept");
                    String declineConcept=msg.getData().getString("decline");

                    break;
                case (MEASURE_MSG):
                    ans = "measure msg "+msg.getData().getString("value");

                    break;
                case (REMINDER_MSG):
                    ans = "this is a reminder msg for : "+msg.getData().getString("value");

                    break;
                case (CALLBACK_MSG):
                     ans = "CALLBACK msg "+msg.getData().getString("value");
                    String Concept=msg.getData().getString("concept");
                    break;
                default:
                    super.handleMessage(msg);
            }

//            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            PopupWindow popupWindow = new PopupWindow(inflater.inflate(R.layout.abc_action_bar_title_item, null, false), 265, (int)(20 * 0.45), true);
//            popupWindow.showAtLocation(new LinearLayout(getBaseContext()), Gravity.BOTTOM, 10, 10);
//            popupWindow.update(50, 50, 300, 80);

//            PopScreen popScreen = new PopScreen();
//            popScreen.startActivity(intent);
//            Toast.makeText(getBaseContext(), ans+" total msg count: "+count, Toast.LENGTH_LONG).show();
//            DialogFragment dialog=MainScreen.BuildDialog.newInstacce(ans);

            //TODO: NEED TO FIX SHOWING DIALOG *****
//            dialog.show(FragmentManager.class.cast(FragmentManager.class), "question");

        }


    }
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
