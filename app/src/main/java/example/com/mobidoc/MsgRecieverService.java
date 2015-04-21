package example.com.mobidoc;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.util.Date;

import example.com.mobidoc.Screens.popUpScreens.MeasurePop;
import example.com.mobidoc.Screens.popUpScreens.PopScreen;
import example.com.mobidoc.Screens.popUpScreens.QuestionPopScreen;


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
        private static final int START_PROJECTION_MSG = 7;

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
                case (START_PROJECTION_MSG):
                    handleStartProjection(msg);
                default:
                    super.handleMessage(msg);
            }




//            Intent intent2 = new Intent(MsgRecieverService.this, QuestionPopScreen.class);
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent2.putExtra("question", "are you ok?");
//            intent2.putExtra("yesAns", "yesAns!!!");
//            intent2.putExtra("noAns", "noAns!!!");
//            getApplicationContext().startActivity(intent2);

        }


    }

    private void handleStartProjection(Message msg) {
        //starting the received projection
        ///========================================
        Log.i("MsgRecieverService","handleStartProjection-recieve projnumber to start :"+msg.getData().getString("projNum"));
        projectionsCollection.getInstance().startProjection(msg.getData().getString("projNum"));

    }

    private void handleCallBack(Message msg) {
        String ans;
        ans = msg.getData().getString("value");
        String Concept = msg.getData().getString("concept");
        Log.i("MsgRecieverService","get CallBack msg with the values (concept: "+Concept+" txt: "+ans+")");
    }

    private void handleReminder(Message msg) {
        String ans;
        ans = "this is a reminder msg for : " + msg.getData().getString("value");
        Log.i("MsgRecieverService","get reminder : "+ans);
    }

    private void handleMeasure(Message msg) {
        String txt;
        txt = msg.getData().getString("value");
        Intent intent = new Intent(this, MeasurePop.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", txt);

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
        String txt =  msg.getData().getString("value");
        Intent intent = new Intent(this, PopScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", txt);
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
                    db.insertMesureToDB(concept,val,date);

                }



        }).start();

    }


    @Override
    public IBinder onBind(Intent intent) {
        db=DB.getInstance(getApplicationContext());
        return mMessenger.getBinder();
    }
}
