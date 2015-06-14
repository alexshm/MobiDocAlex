package example.com.mobidoc;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.Date;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.CommunicationLayer.PushNotification;
import example.com.mobidoc.CommunicationLayer.ServicesToBeDSS.PicardCommunicationLayer;
import example.com.mobidoc.CommunicationLayer.pushNotificationServices.GcmBroadcastReceiver;
import example.com.mobidoc.Screens.popUpScreens.MeasurePop;
import example.com.mobidoc.Screens.popUpScreens.PopScreen;
import example.com.mobidoc.Screens.popUpScreens.RecommendationPopScreen;
import example.com.mobidoc.Screens.popUpScreens.YesNoQuestion;
import projections.Utils;


public class MsgRecieverService extends Service {


    public int count = 0;
    // Messenger Object that receives Messages from connected clients
    IncomingMsgHandler incomingMsgHandler = new IncomingMsgHandler();
    Messenger mMessenger = new Messenger(new IncomingMsgHandler());
    String baseUrl;
    OpenMrsApi openMrsApi ;
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
                    handleRecommendation(msg);

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



        }


    }

    private void handleCallBack(Message msg) {
        String ans;
        ans = msg.getData().getString("value");
        final String Concept = msg.getData().getString("concept");
        Log.i("MsgRecieverService", "get CallBack msg with the values (concept: " + Concept + " txt: " + ans + ")");
        Toast.makeText(getApplicationContext()," A CallBack is happened. Send A CallBack mssg to the Server",Toast.LENGTH_SHORT).show();
        baseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        openMrsApi = new OpenMrsApi(baseUrl);
        if(Concept.equals("5169")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = new ConfigReader(getApplicationContext()).getProperties().getProperty("Picard_WCF_URL");
                    String patientID = openMrsApi.getPatintUuid().replaceAll("\\-","");
                    String startTime = "2014-02-01T19:42:18+02:00";
                    String appID = PushNotification.getInstance(getApplicationContext()).getMobileID();
                    new NotifyBe_DssTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, patientID, startTime, appID);

                }
            }).start();


        }
    }

    private void handleReminder(Message msg) {
        String ans;
        ans = "this is a reminder for : " + msg.getData().getString("value");
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
        intent.putExtra("concept", concept);
        getApplicationContext().startActivity(intent);

    }

    private void handleRecommendation(Message msg) {
        String recommendation;
        recommendation =  msg.getData().getString("value");
        String acceptConcept = msg.getData().getString("accept");
        String declineConcept = msg.getData().getString("decline");
        Intent intent = new Intent(this, RecommendationPopScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("acceptConcept", acceptConcept);
        intent.putExtra("declineConcept", declineConcept);
        intent.putExtra("recommendation",recommendation);
        getApplicationContext().startActivity(intent);

    }

    private void handleNotification(Message msg) {
        String txt = msg.getData().getString("value");
        Intent intent = new Intent(this, PopScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", txt);
        getApplicationContext().startActivity(intent);
    }

    private void handleQuestion(Message msg) {

        String qustion;
        qustion = msg.getData().getString("value");
        String yesConcept = msg.getData().getString("yes");
        String noConcept = msg.getData().getString("no");
        Intent intent = new Intent(this, YesNoQuestion.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(yesConcept !=null && noConcept !=null) {
            intent.putExtra("yesConcept", yesConcept);
            intent.putExtra("noConcept", noConcept);
            intent.putExtra("question", qustion);
            try {
                getApplicationContext().startActivity(intent);
            }
            catch (Exception e){
                Log.i("MsgRecieverService",e.getMessage());

            }
        }
        else{
            Log.i("MsgRecieverService","got null values ");
        }
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
            String parsedMsg= Utils.ConvertHexToString(Receivedmsg);
            Log.i("MsgRecieverService", "receive : "+parsedMsg);
            try {
                JSONObject jObject = new JSONObject(parsedMsg);
                String txt = jObject.getString("description");
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
    private class NotifyBe_DssTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {


            String url = params[0];
            String PatientID = params[1];
            String startTime = params[2];
            String appID =params[3];
            PicardCommunicationLayer.DataNotificationOnCallBack(PatientID, "5170", startTime, appID, url);
            return "";
        }
    }

}
