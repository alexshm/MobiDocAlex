package example.com.mobidoc.Screens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.CommunicationLayer.PushNotification;
import example.com.mobidoc.CommunicationLayer.ServicesToBeDSS.PicardCommunicationLayer;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.LoginTask;
import example.com.mobidoc.R;
import example.com.mobidoc.projectionsCollection;

@SuppressLint("ShowToast")
public class MainScreen extends Activity {
    TextView t = null;
    BroadcastReceiver projectionRec;
    ProgressDialog startGLDialog;
    OpenMrsApi mrs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        IntentFilter intentFilter = new IntentFilter("startProjection");
        final String BaseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        mrs=new OpenMrsApi(BaseUrl);

        projectionRec=new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                final String projnum=intent.getStringExtra("projNum");
                if(intent.getAction().contains("start"))
                {
                    Log.i("mainScreen-onReceive","onReceive from broadcast projectionReceiver-need to start proj# "+projnum);

                    projectionsCollection.getInstance().getprojection(projnum).startProjection();
                }
                else
                {
                    Log.i("mainScreen-onReceive","need to Stop proj# "+projnum);

                    projectionsCollection.getInstance().stopProjection(projnum);
                }
            }
        };
        getApplicationContext().registerReceiver(projectionRec,intentFilter);
        Log.i("Main Screen","register to brodcastRec for recieve projections");
        new registerDeviceAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //register the Device to the GCM service



    }

    private class registerDeviceAsyncTask extends AsyncTask<Void, Void, String> {

        @Override

        protected String doInBackground(Void... params) {

            PushNotification p = PushNotification.getInstance(getApplicationContext());
            p.registerDevice();
            Log.i("MainScreen", "app ID: " + p.getMobileID());
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("MainScreen","finish register Mobile");
        }
    }


    public void showDialogFromService(final String message) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                t.post(new Runnable() {

                    @Override
                    public void run() {
                        DialogFragment dialog = BuildDialog.newInstacce(message);
                        dialog.show(getFragmentManager(), "question");
                    }


                });
            }
        }).start();
    }

    public void goToTests(View view) {
        Intent SimulationScreen = new Intent(MainScreen.this, example.com.mobidoc.SimulationScreen.class);
        startActivity(SimulationScreen);
    }

    public void startGuideLine(View view) {
        Properties prop = new ConfigReader(getApplicationContext()).getProperties();
        final String url = prop.getProperty("Picard_WCF_URL");
        final String glid = prop.getProperty("Guide_Line_ID_To_Run");
        final String regid=PushNotification.getInstance(getApplicationContext()).getMobileID();

        final String startTime = "20-05-2015 08:00:00";

        showDialog(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String patientID = mrs.getPatintUuid().replaceAll("\\-","");
                boolean result = PicardCommunicationLayer.StartGuideLine(patientID, startTime, glid,regid, url);
                Log.i("Main Screen", "get the result from starting guide line " + result);

                startGLDialog.dismiss();
                if (!result) {
                    String alertMsg = "Error starting Guide Line. Try Again or check Internet Connection";
                    AlertDialogFragment d = AlertDialogFragment.newInstance(alertMsg);
                    d.show(getFragmentManager(), "Alert");
                    return;
                }
            }
        }).start();
    }

    public void goToSettings(View view) {
        String alertMsg="This option is not yet available.";
        AlertDialogFragment d=AlertDialogFragment.newInstance(alertMsg);
        d.show(getFragmentManager(), "Alert");
        return;
    }

    public void goToWebTests(View view) {
        Intent webScreen = new Intent(MainScreen.this, webComScreen.class);
        startActivity(webScreen);
    }

    public void measureClick(View view) {
        //String alertMsg="This option is not yet available.";
        //AlertDialogFragment d=AlertDialogFragment.newInstance(alertMsg);
        //d.show(getFragmentManager(), "Alert");
        //return;
        //TODO: measures screen
        Intent measureScreen = new Intent(MainScreen.this, MeasuresScreen.class);
        startActivity(measureScreen);
    }



    // Class that creates the AlertDialog
    public static class AlertDialogFragment extends DialogFragment {
        private static String msg;


        public static AlertDialogFragment newInstance(String _msg) {
           msg=_msg;
            return new AlertDialogFragment();
        }

        // Build AlertDialog using AlertDialog.Builder
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(msg)

                            // User cannot dismiss dialog by hitting back button
                    .setCancelable(false)

                            // Set up No Button
                    .setNeutralButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();


        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        startGLDialog = new ProgressDialog(this);
        // Set Dialog message
        startGLDialog.setMessage("Please Wait to receive Projections..");
        startGLDialog.setTitle("Starting Guide Line at the BE-DSS...");

        // Dialog will be displayed for an unknown amount of time
        startGLDialog.setIndeterminate(true);
        startGLDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        startGLDialog.setCancelable(false);
        startGLDialog.show();
        return startGLDialog;

    }
    public static class BuildDialog extends DialogFragment {

        private static String msg;


        public static BuildDialog newInstacce(String _msg) {

            msg = _msg;
            return new BuildDialog();

        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String msg = this.msg;
            return new AlertDialog.Builder(getActivity())
                    .setMessage(msg)
                    .setCancelable(false)
                    .setNegativeButton("No(or other)",
                            //sets the no - button and the action to do with that

                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(getActivity(), "you pressed on No", Toast.LENGTH_LONG).show();
                                }
                            })
                    .setPositiveButton("Yes(bla bla)",

                            //sets the Yes - button and the action to do with that

                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(getActivity(), "you pressed on Yes", Toast.LENGTH_LONG).show();
                                }
                            }).create();


        }
    }

}