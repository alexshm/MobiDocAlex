package example.com.mobidoc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import dalvik.system.DexClassLoader;
import javassist.ClassPool;
import projections.Actions.Action;
import projections.Actions.MeasurementAction;
import projections.Actions.NotificationAction;
import projections.Actions.QuestionAction;
import projections.mobiDocProjections.ProjectionBuilder;
import projections.projection;

@SuppressLint("ShowToast")
public class SimulationScreen extends Activity {
    static final int GENERATE_WITH_DEXMAKER = 1;
    static final int GENERATE_JAVA_ASSIST = 2;
    final BlockingQueue<String> q1 = new ArrayBlockingQueue<String>(1000);
    private EditText t;
    private RadioButton katProj;
    private RadioButton bgProj;
    private RadioButton monitorProj;
    Messenger mMsg = null;
    private EditText everyXtxt;
    private EditText remaindertxt;
    private EditText startTimetxt;
    private Spinner spinner;
    private Spinner projections_spinner;
    private Spinner reminder_spinner;
    private String selectedProjection;
    private ArrayAdapter<CharSequence> projectionVals;
    private String projectionId;
    private int count = 1;

    // Intent used for binding to LoggingService
    private Intent serviceIntent;
    private ProjectionBuilder pb;
    private Messenger mMessengerToLoggingService;
    private boolean mIsBound;

    // Object implementing Service Connection callbacks
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessengerToLoggingService = new Messenger(service);
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessengerToLoggingService = null;
            mIsBound = false;
        }
    };


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simulation_screen);
        serviceIntent = new Intent(this.getApplicationContext(), example.com.mobidoc.MsgRecieverService.class);

        pb = new ProjectionBuilder(this.getApplicationContext());
        final Button startbtn = (Button) findViewById(R.id.startSimulation);
        final Button handlerSender = (Button) findViewById(R.id.button2);

        // projections spinner
        ////===========
        projections_spinner = (Spinner) findViewById(R.id.spinner3);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterprojections = ArrayAdapter.createFromResource(this,
                R.array.projections, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterprojections.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        projections_spinner.setAdapter(adapterprojections);
        //=================================================

        projectionVals = ArrayAdapter.createFromResource(this,
                R.array.projectionsVals, android.R.layout.simple_spinner_item);


        projections_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedProjection = parentView.getItemAtPosition(position).toString();
                projectionId = projectionVals.getItem(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        handlerSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mIsBound) {

                    // Send Message to the Logging Service
                    SendActionToHandler();

                }

            }
        });

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimulateProjections(v);

            }
        });


    }

    private void SendActionToHandler() {
//        Action a = new MeasurementAction("ss", "5088", getApplicationContext());
//        Action a = new NotificationAction("Dont forget your pills" ,"5088",Action.Actor.Patient,getApplicationContext());
//        Action a = new QuestionAction("what is your name?" ,"5088",getApplicationContext());
        Action a = new MeasurementAction("what is your name?", "5088");

        try {
            Message msg = a.call();
            mMessengerToLoggingService.send(msg);

        } catch (RemoteException e) {
            Log.e("SendActionToHandler", "error sending msg");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void SimulateProjections(View v) {

        switch (projectionId) {
            case "20119":
                Simulate2abnormalWeek();
                break;
            case "19965":
                Simulate2positiveKetInAWeek();
                break;

        }
    }

    private void Simulate2abnormalWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
        Calendar c=Calendar.getInstance();
        c.set(2014,2,1,8,0);
        insertingMeasure("4985", "160", c.getTime());
       // try {
       //     Thread.sleep(1000);
     //    c.set(2014,2,4,8,0);
        //    insertingMeasure("4985","170",c.getTime());
      //  }
       // catch (Exception e)
      //  {

     //   }






    }

    private void Simulate2positiveKetInAWeek() {


    }

    private void insertingMeasure(String concept, String value,Date time) {
    //simulate insertion
       Intent i = new Intent(concept);
       i.putExtra("concept", concept);
       SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
       String timeStr = sdf.format(time);

       i.putExtra("time", timeStr);
       i.putExtra("value",value);
       sendBroadcast(i, android.Manifest.permission.VIBRATE);

    }
    private String readProjectionTxt(String projId) {
        try {
            //=================================
            // read file   data from raw resources
            // TODO: read data from input streamer that was recived from the web(server) and not from the raw resorces
            //==========================

            InputStream iS;

            int rID = getResources().getIdentifier("example.com.mobidoc:raw/p" + projId, null, null);
            iS = getResources().openRawResource(rID);

            //create a buffer that has the same size as the InputStream
            byte[] buffer = new byte[iS.available()];
            //read the text file as a stream, into the buffer
            iS.read(buffer);
            //create a output stream to write the buffer into
            ByteArrayOutputStream oS = new ByteArrayOutputStream();
            //write this buffer to the output stream
            oS.write(buffer);
            //Close the Input and Output streams
            oS.close();
            iS.close();

            //return the output stream as a String
            return oS.toString();

        } catch (IOException e) {
            Log.e("Simulation screen", "error reading file: " + projId);
            return null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Intent serviceIntent = new Intent(this,example.com.mobidoc.MsgRecieverService.class);


        bindService(serviceIntent, mConnection,
                Context.BIND_AUTO_CREATE);

    }


    @Override
    protected void onPause() {

        if (mIsBound)
            unbindService(mConnection);

        super.onPause();
    }


}