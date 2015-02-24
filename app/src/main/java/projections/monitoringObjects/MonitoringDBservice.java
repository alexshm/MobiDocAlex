package projections.monitoringObjects;


import android.app.DialogFragment;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;


public class MonitoringDBservice extends Service {


        // Messenger Object that receives Messages from connected clients
        Messenger monitoringMessenger = new Messenger(new IncomingMsgHandler());


        class IncomingMsgHandler extends Handler {


            private static final int NO_VAR = 1;
            private static final int CYCLIC = 2;
            private static final int MONITOR = 3;


            @Override
            public void handleMessage(Message msg) {
                String ans="";
                String concept="";
                String val="";
                String var="";

                switch (msg.what) {
                    case (NO_VAR):
                            //need only to save the data in the DB
                        System.out.println("the msg recieeced!!!!!");

                        break;
                    case (CYCLIC):

                        System.out.println("the msg recieeced2222222222!!!!!");
                        break;
                    case (MONITOR):
                            concept=msg.getData().getString("value");
                            val=msg.getData().getString("value");
                            var=msg.getData().getString("var");
                            String op=var.split("#")[0];
                            String desc=var.split("#")[1];

                        System.out.println("the arrived msg is :\n\n");
                        System.out.println("op: "+op+"  descc: "+desc);
                        break;

                    default:
                        super.handleMessage(msg);
                }


                Toast.makeText(getBaseContext(), ans + " total msg count: " + 7, Toast.LENGTH_LONG).show();

                saveToDB("","","");

            }


        }


        private void saveToDB(String concept,String val,String date)
        {
            //TODO: function to saave data to the DB+  to internal file in the SDCARD
        }
        @Override
        public IBinder onBind(Intent intent) {

            return monitoringMessenger.getBinder();
        }
    }

