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


    public int count = 0;
    // Messenger Object that receives Messages from connected clients
    IncomingMsgHandler incomingMsgHandler = new IncomingMsgHandler();
    Messenger mMessenger = new Messenger(new IncomingMsgHandler());


    class IncomingMsgHandler extends Handler {

        private static final int MEASURE_MSG = 4;
        private static final int QUESTION_MSG = 1;
        private static final int RECOMMENDATION_MSG = 2;
        private static final int NOTIFICATION_MSG = 3;
        private static final int REMINDER_MSG = 6;
        private static final int CALLBACK_MSG = 5;

        @Override
        public void handleMessage(Message msg) {
            String ans = "";

            count++;
            switch (msg.what) {
                case (NOTIFICATION_MSG):
                    ans = "notificatin msg " + msg.getData().getString("value");
                    Intent intent = new Intent(MsgRecieverService.this,PopScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("msg", "Take some drugs");
                    getApplicationContext().startActivity(intent);

                    break;
                case (QUESTION_MSG):
                    ans = "question msg " + msg.getData().getString("question");
                    String yesAns = msg.getData().getString("yesVal");
                    String noAns = msg.getData().getString("noVal");
                    Intent intent2 = new Intent(MsgRecieverService.this,QuestionPopScreen.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2.putExtra("question", "are you ok?");
                    intent2.putExtra("yesAns", "yesAns!!!");
                    intent2.putExtra("noAns", "noAns!!!");
                    getApplicationContext().startActivity(intent2);
                    break;
                case (RECOMMENDATION_MSG):

                    ans = "reccomendation msg " + msg.getData().getString("value");
                    String acceptConcept = msg.getData().getString("accept");
                    String declineConcept = msg.getData().getString("decline");

                    break;
                case (MEASURE_MSG):
                    ans = "measure msg " + msg.getData().getString("value");

                    break;
                case (REMINDER_MSG):
                    ans = "this is a reminder msg for : " + msg.getData().getString("value");

                    break;
                case (CALLBACK_MSG):
                    ans = "CALLBACK msg " + msg.getData().getString("value");
                    String Concept = msg.getData().getString("concept");
                    break;
                default:
                    super.handleMessage(msg);
            }



            //TODO: NEED TO FIX SHOWING DIALOG *****

            Intent intent2 = new Intent(MsgRecieverService.this,QuestionPopScreen.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.putExtra("question", "are you ok?");
            intent2.putExtra("yesAns", "yesAns!!!");
            intent2.putExtra("noAns", "noAns!!!");
            getApplicationContext().startActivity(intent2);

        }



    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
