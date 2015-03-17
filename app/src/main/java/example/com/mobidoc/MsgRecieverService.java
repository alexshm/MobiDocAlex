package example.com.mobidoc;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;


public class MsgRecieverService extends Service {

    public int count = 0;
    // Messenger Object that receives Messages from connected clients
    IncomingMsgHandler incomingMsgHandler = new IncomingMsgHandler();
    Messenger mMessenger = new Messenger(new IncomingMsgHandler());
    private DB db;

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
                    handleNotification(msg);
                    break;
                case (QUESTION_MSG):
                    handleQuestion(msg);
                    break;
                case (RECOMMENDATION_MSG):
                    handleRecomendation(msg);

                    break;
                case (MEASURE_MSG):
                    handleMeasure(msg);

                    break;
                case (REMINDER_MSG):
                    handleReminder(msg);

                    break;
                case (CALLBACK_MSG):
                    handleCallBack(msg);
                    break;
                default:
                    super.handleMessage(msg);
            }


            //TODO: NEED TO FIX SHOWING DIALOG *****

//            Intent intent2 = new Intent(MsgRecieverService.this, QuestionPopScreen.class);
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent2.putExtra("question", "are you ok?");
//            intent2.putExtra("yesAns", "yesAns!!!");
//            intent2.putExtra("noAns", "noAns!!!");
//            getApplicationContext().startActivity(intent2);

        }


    }

    private void handleCallBack(Message msg) {
        String ans;
        ans = "CALLBACK msg " + msg.getData().getString("value");
        String Concept = msg.getData().getString("concept");
    }

    private void handleReminder(Message msg) {
        String ans;
        ans = "this is a reminder msg for : " + msg.getData().getString("value");
    }

    private void handleMeasure(Message msg) {
        String ans;
        ans = "measure msg " + msg.getData().getString("value");
        Intent intent = new Intent(this, MessurePop.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", "Take some drugs");

        getApplicationContext().startActivity(intent);

    }

    private void handleRecomendation(Message msg) {
        String ans;
        ans = "reccomendation msg " + msg.getData().getString("value");
        String acceptConcept = msg.getData().getString("accept");
        String declineConcept = msg.getData().getString("decline");
    }

    private void handleNotification(Message msg) {
        String ans;
        ans = "notificatin msg " + msg.getData().getString("value");
        Intent intent = new Intent(this, PopScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", "Take some drugs");
        getApplicationContext().startActivity(intent);
    }

    private void handleQuestion(Message msg) {
        String ans;
        ans = "question msg " + msg.getData().getString("question");
        String yesAns = msg.getData().getString("yesVal");
        String noAns = msg.getData().getString("noVal");
        Intent intent2 = new Intent(this, QuestionPopScreen.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.putExtra("question", "are you ok?");
        intent2.putExtra("yesAns", "yesAns!!!");
        intent2.putExtra("noAns", "noAns!!!");
        getApplicationContext().startActivity(intent2);
    }


    private void saveToDB(final String concept, final String val, final Date date)
    {
        //saving the data to DB/ SDCARD
        ///========================================
        new Thread(new Runnable() {
            @Override
            public void run() {
                    db.insertToDB(concept,val,date);

                }



        }).start();

    }


    @Override
    public IBinder onBind(Intent intent) {
        db=new DB(getApplicationContext());
        return mMessenger.getBinder();
    }
}
