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
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
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

    String SimulationData="";
    // Intent used for binding to LoggingService
    private Intent serviceIntent;
    private ProjectionBuilder pb;
    private Messenger mMessengerToLoggingService;
    private boolean mIsBound;
    Thread simulationThread;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadSimulationData();
                Log.i("Simulation screen","finish loading simulation data");
            }
        }).start();
        setContentView(R.layout.simulation_screen);
        serviceIntent = new Intent(this.getApplicationContext(), example.com.mobidoc.MsgRecieverService.class);

        pb = new ProjectionBuilder(this.getApplicationContext());
        final Button startbtn = (Button) findViewById(R.id.startSimulation);
        final Button handlerSender = (Button) findViewById(R.id.button2);
        final ImageButton playsim = (ImageButton) findViewById(R.id.simPlay);
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
        playsim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSimulation(v);
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

    private void runSimulation() {
        simulationThread=new Thread(new Runnable() {
            @Override
            public void run() {
                String[] simdata = SimulationData.split("\n");
                for (int i = 1; i < simdata.length; i++) {
                    String[] values=simdata[i].split(";");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String concept=values[1];
                    String value=values[2];
                    try {
                        Date SimTime= sdf.parse(values[0]);
                       // Log.i("simulation screen","insert data : ("+concept+", "+value+", "+SimTime);
                        insertingMeasure(concept, value, SimTime);
                        Thread.sleep(2500);

                    } catch (Exception e) {
                        Log.e("Simlation Screen","run simulation error. error msg : "+e.getMessage());
                    }

                }
            }
        });



    }


    private String readFileFromSDCARD(File f) {
        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;

            while ((line = br.readLine()) != null) {
                if(!line.contains("//")) {
                    text.append(line);
                    text.append('\n');
                }
            }
            return text.toString();
        } catch (IOException e) {
            Log.e("Simulation Screen", "error reading the simulation file from SDCARD. error msg : " + e.getMessage());
            return "";
        }
    }


    private boolean copySimulationFile(String dstFolder)

    {
        FileOutputStream outStream=null;
        File destFile = new File(dstFolder);
        File folder = new File(Environment.getExternalStorageDirectory() + "/MobiDoc");
        if (!folder.exists()) {
            folder.mkdir();
            copySimulationFile(folder.getAbsolutePath()+"/simulationfile.txt");
        }

        if(!destFile.exists()) {
            try {
                destFile.createNewFile();
            } catch (IOException e) {
                Log.e("Simulation Screen", "error while creating the new simulation file.error msg is : " + e.getMessage());
                return false;
            }
        }

        try {
            outStream = new FileOutputStream(destFile);
            outStream.write(SimulationData.getBytes());
            outStream.close();
            return true;

        } catch (FileNotFoundException e) {
            Log.e("Simulation Screen", "error while copying file when trying to copy from local to new folder. error msg is : " + e.getMessage());
            return false;
        }
        catch (IOException e) {
            Log.e("Simulation Screen", "error while copying file when trying to copy from local to new folder. error msg is : " + e.getMessage());
            return false;
        }
    }

    private void startSimulation(View v)
    {
        if( simulationThread!=null)
            simulationThread.start();
        else {
            runSimulation();
           simulationThread.start();
        }
    }

    private void pauseSimulation()
    {
        try {
            simulationThread.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void resumeSimulation()
    {
        simulationThread.notify();
    }
    private void stopSimulation()
    {
        try {
            simulationThread.sleep(500);
            simulationThread.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    private  void loadSimulationData() {
        //check if the file and folder is existing.
        // if not creates them and copy the simulation file to this directory
        // if the file is allready exists. reads its data
        boolean success = true;
        File simulationFile = new File(Environment.getExternalStorageDirectory() + "/MobiDoc/simulationfile.txt");
        if (simulationFile.exists()) {
            SimulationData = readFileFromSDCARD(simulationFile);
            Log.i("Simulation Screen","finish reading the simulation data from SDCARD successfully");
        } else {
            SimulationData = readLocalFile("simulationfile");
            success = copySimulationFile(simulationFile.getAbsolutePath());
            if (success) {
                Log.i("Simulation Screen","the simulation file created and copied successfully");
            } else {
                Log.e("Simulation Screen", "error creating or coping the simulation file to the SDCARD");
            }
        }
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
                Calendar c=Calendar.getInstance();
                c.set(2014,2,1,8,0);
                insertingMeasure("4985", "160", c.getTime());
                try {
                    Thread.sleep(2500);
                    c.set(2014,2,1,12,0);
                    insertingMeasure("4986","170",c.getTime());
                }
                catch (Exception e) {
                }

            }
        }).start();



        //   }






    }

    private void Simulate2positiveKetInAWeek() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
                Calendar c = Calendar.getInstance();
                c.set(2014, 2, 1, 8, 0);
                insertingMeasure("5021", "++", c.getTime());
                try {
                    Thread.sleep(3500);
                    c.set(2014, 2, 1, 12, 0);
                    insertingMeasure("5021", "++", c.getTime());
                } catch (Exception e) {
                }

            }
        }).start();

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
    private String readLocalFile(String projId) {
        try {
            //=================================
            // read file   data from raw resources
            // TODO: read data from input streamer that was recived from the web(server) and not from the raw resorces
            //==========================

            InputStream iS;

            int rID = getResources().getIdentifier("example.com.mobidoc:raw/" + projId, null, null);
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