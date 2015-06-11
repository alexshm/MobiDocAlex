/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package example.com.mobidoc.CommunicationLayer.pushNotificationServices;



import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.Screens.MainScreen;
import example.com.mobidoc.projectionsCollection;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import example.com.mobidoc.R;
import projections.ScriptingLayer.JsScriptExecutor;
import projections.Utils;
import projections.projection;
/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private static final int START_PROJECTION_MSG = 7;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private OpenMrsApi mrsApi;

    public GcmIntentService() {
       super("GcmIntentService");
        String url = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        mrsApi=new  OpenMrsApi(url);
    }

    public static final String TAG = "GCM Demo";


    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

       // this.serviceIntent  = new Intent(getApplicationContext(), example.com.mobidoc.MsgRecieverService.class);
       // startService();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " , extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " , extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                                     // This loop represents the service doing some work.
                      for (int i = 0; i < 3; i++) {
                          try{

                              Thread.sleep(300);

                          } catch (InterruptedException e) {

                    }
                }
                Log.i(TAG, "Complete receiving projections in : " + SystemClock.elapsedRealtime());
                //string contains all the received projections from the server
                String Receivedmsg=extras.getString("message");
                  String parsedMsg= Utils.ConvertHexToString(Receivedmsg);
                if(parsedMsg.contains("beginProjection"))
                {
                    String[] preferences=mrsApi.getPreferences();
                    String[] projectionScript=parsedMsg.split("beginProjection");
                    Log.i(TAG, "Received : "+(projectionScript.length-1)+" projections from server");

                    for(int i=1;i<projectionScript.length;i++)
                    {
                        final String script="beginProjection"+projectionScript[i];

                         new ProjectionScriptExecuter(preferences).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,script);


                    }

                }
                // Post notification of received message.
               sendNotification(extras.getString("type"),extras.getString("projnumber"));

              //  Log.i(TAG, "Received: " + extras.getString("message"));
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String projName,String projnumber ) {
       String msgToSend ="Received new projections";
        Log.i(TAG,msgToSend);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainScreen.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.mobidocicon2)
        .setContentTitle("Projection Notification")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText("new projection Received"))
        .setContentText(projName+" ("+projnumber+")")
        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    private class ProjectionScriptExecuter extends AsyncTask<String, Void, projection> {


        JsScriptExecutor jsScript;
        String[] preferences;
         public ProjectionScriptExecuter( String[] prefs)
         {
             preferences=prefs;
            jsScript=new JsScriptExecutor(getApplicationContext(),preferences);
         }

            @Override
            protected projection doInBackground(String... params) {
                //Log.i("GCM service", " parsing the projection : "+params[0]);
                String script = params[0];
                projection proj=jsScript.runScript(script);
                return proj;

            }

            @Override
            protected void onPostExecute(projection proj) {

                Log.i("GCM service", " finish building projection: "+proj.getProjectionName()+"("+proj.getProjectionId()+")");
                projectionsCollection.getInstance().addProjection(proj);
                Log.i("GCM service", "adding : "+proj.getProjectionName()+"("+proj.getProjectionId()+") to projectionCollection");
                sendStartProjectionMsg(proj.getProjectionId());
            }

            private void sendStartProjectionMsg(String projectionId)
            {

                Intent brdcastIntent = new Intent("startProjection");
                brdcastIntent.setAction("startProjection");
                brdcastIntent.putExtra("projNum",projectionId);
                sendBroadcast(brdcastIntent, android.Manifest.permission.VIBRATE);

            }

    }


}
