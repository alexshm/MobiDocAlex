package projections.monitoringObjects;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MonitoringDBservice extends Service {


        // Messenger Object that receives Messages from connected clients
        Messenger monitoringMessenger = new Messenger(new IncomingMsgHandler());


        class IncomingMsgHandler extends Handler {

            @Override
            public void handleMessage(Message msg) {
                try {
                    //extracting  msg data

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");

                    String concept=msg.getData().getString("concept");
                    String val=msg.getData().getString("value");
                    String time = msg.getData().getString("time");
                    Date dateNow = sdf.parse(time);
                    saveToSDCard(concept, val, dateNow);
                   // saveToDB(concept,val,dateNow);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


        }

    private void saveToSDCard(final String concept, final String val, final Date date)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    File myFile = new File("/sdcard/MobiDoc/data.txt");
                    myFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(myFile);
                    OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
                    myOutWriter.append( "#concept : "+concept+" val: "+val+"time: "+date.toString());
                    myOutWriter.close();
                    fOut.close();
                    Log.e("MonitoringDBservice","saving the data to the SDCARD . "+
                            "concept : "+concept+" val: "+val+"time: "+date.toString());

                }
                catch (Exception e)
                {
                    Log.e("MonitoringDBservice","error data in SDCARD. tha data that was trying to be saved is "+
                    "concept : "+concept+" val: "+val+"time: "+date.toString());
                }
            }
        });



    }




        @Override
        public IBinder onBind(Intent intent) {

            return monitoringMessenger.getBinder();
        }
    }

