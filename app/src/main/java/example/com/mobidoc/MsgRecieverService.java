package example.com.mobidoc;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Date;

import example.com.mobidoc.CommunicationLayer.pushNotificationServices.GcmBroadcastReceiver;
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
        Log.i("MsgRecieverService", "handleStartProjection-recieve projnumber to start :" + msg.getData().getString("projNum"));
        projectionsCollection.getInstance().startProjection(msg.getData().getString("projNum"));

    }

    private void handleCallBack(Message msg) {
        String ans;
        ans = msg.getData().getString("value");
        String Concept = msg.getData().getString("concept");
        Log.i("MsgRecieverService", "get CallBack msg with the values (concept: " + Concept + " txt: " + ans + ")");
    }

    private void handleReminder(Message msg) {
        String ans;
        ans = "this is a reminder msg for : " + msg.getData().getString("value");

        Log.i("MsgRecieverService", "get reminder : " + ans);
        Intent intent = new Intent(this, PopScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", ans);
        getApplicationContext().startActivity(intent);
    }

    private void handleMeasure(Message msg) {
        String txt;
        txt = msg.getData().getString("value");
        String concept = msg.getData().getString("concept");

        Intent intent = new Intent(this, MeasurePop.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", txt);
        intent.putExtra("measureType", concept);
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
        String txt = msg.getData().getString("value");
        Intent intent = new Intent(this, PopScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", txt);
        getApplicationContext().startActivity(intent);
    }

    private void handleQuestion(Message msg) {
        String ans;
        ans = "question msg " + msg.getData().getString("value");
        String yesAns = msg.getData().getString("yes");
        String noAns = msg.getData().getString("no");
        Intent intent2 = new Intent(this, QuestionPopScreen.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.putExtra("question", "are you ok?");
        intent2.putExtra("yesAns", noAns);
        intent2.putExtra("noAns", yesAns);
          getApplicationContext().startActivity(intent2);
    }


    private void saveToDB(final String concept, final String val, final Date date) {
        //saving the data to DB/ SDCARD
        ///========================================
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.insertMesureToDB(concept, val, date);

            }


        }).start();

    }


    @Override
    public IBinder onBind(Intent intent) {
        db = DB.getInstance(getApplicationContext());
        return mMessenger.getBinder();
    }



    @Deprecated
    public void onStart(Intent intent, int startId) {


        if(intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
            Log.i("MsgRecieverService", "receive new Notification msg from server ");
            Bundle extras = intent.getExtras();
            String Receivedmsg = extras.getString("message");
            try {
                JSONObject jObject = new JSONObject(Receivedmsg);
                String txt = jObject.getString("txt");
                int type = jObject.getInt("type");
                String concept = jObject.getString("concept");
                Message msg = Message.obtain(null, type, 0, 0, 0);
                Bundle bundle = new Bundle();
                bundle.putString("value", txt);
                bundle.putString("concept", concept);

                msg.setData(bundle);
                GcmBroadcastReceiver.completeWakefulIntent(intent);
                this.incomingMsgHandler.handleMessage(msg);

            } catch (JSONException e) {
                Log.e("MsgRecieverService", "error parsing the msg Json ");
            }
        }






    }

}
