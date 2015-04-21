package example.com.mobidoc.Screens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import example.com.mobidoc.CommunicationLayer.PushNotification;
import example.com.mobidoc.R;
import example.com.mobidoc.projectionsCollection;

@SuppressLint("ShowToast")
public class MainScreen extends Activity {
    TextView t = null;
    BroadcastReceiver projectionRec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        IntentFilter intentFilter = new IntentFilter("startProjection");


        projectionRec=new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                final String projnum=intent.getStringExtra("projNum");
                Log.i("mainScreen-onReceive","onReceive from broadcast projectionReceiver-need to start proj# "+projnum);

                projectionsCollection.getInstance().getprojection(projnum).startProjection();

            }
        };
        getApplicationContext().registerReceiver(projectionRec,intentFilter);
        //register to triggring timer events
        Log.i("Main Screen","register to brodcastRec for recieve projections");


        new Thread(new Runnable() {
            @Override
            public void run() {
                //recieve notification only after login
                PushNotification p = PushNotification.getInstance(getApplicationContext());
                p.registerDevice();
                Log.i("MainScreen", "app ID: " + p.getMobileID());
            }
        }).start();
    }


    private void showToastFromService(final String message) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                t.post(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(MainScreen.this.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }


                });
            }
        }).start();
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

    public void goToSettings(View view) {

    }

    public void goToWebTests(View view) {
        Intent webScreen = new Intent(MainScreen.this, webComScreen.class);
        startActivity(webScreen);
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