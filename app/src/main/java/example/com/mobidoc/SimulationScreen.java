package example.com.mobidoc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;


import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import projections.Actions.Action;
import projections.Actions.MeasurementAction;


@SuppressLint("ShowToast")
public class SimulationScreen extends Activity {

    private EditText t;
    Messenger mMsg = null;
    private Spinner projections_spinner;
    private String selectedProjection;
    private ArrayAdapter<CharSequence> projectionVals;
    private String projectionId;

    int count=0;
    String SimulationData="";
    // Intent used for binding to LoggingService
    private Intent serviceIntent;

    private Messenger mMessengerToLoggingService;
    private boolean mIsBound;
    private boolean isPasued;
    private String prevAction;
    Thread simulationThread;
    Object obj=new Object();
    ImageButton playsim ;
    ImageButton stopSim;
    TextView playTxt;
    TextView simTime;
    TextView simvalue;
    TextView simconcept;
    OpenMrsApi mrs;
    AlarmManager am;
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
        prevAction="init";
        setContentView(R.layout.simulation_screen);
        isPasued=false;

        playsim = (ImageButton) findViewById(R.id.simPlay);
       stopSim = (ImageButton) findViewById(R.id.simStop);
        playTxt = (TextView) findViewById(R.id.textView3);
        simconcept = (TextView) findViewById(R.id.textView14);
        simvalue = (TextView) findViewById(R.id.textView15);
        simTime = (TextView) findViewById(R.id.textView16);
        //final ImageButton pauseSim = (ImageButton) findViewById(R.id.simPause);



        serviceIntent = new Intent(this.getApplicationContext(), example.com.mobidoc.MsgRecieverService.class);

        String url=new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        mrs=new OpenMrsApi(url);

        projectionVals = ArrayAdapter.createFromResource(this,
                R.array.projectionsVals, android.R.layout.simple_spinner_item);

        new  loadSimulationDataTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        playsim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSimulation(v);
            }
        });

        stopSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSimulation();
                prevAction="stop";
            }
        });




    }

    private void runSimulation() {
        simulationThread=new Thread(new Runnable() {

            @Deprecated
            public final void resume() {
                notify();
            }



            @Override
            public void run() {
                    Calendar c=Calendar.getInstance();
                    String[] simdata = SimulationData.split("\n");
                    for (int i = 1; i < simdata.length; i++) {
                        final String[] values = simdata[i].split(";");
                        final String concept = values[1];
                        final String value = values[2];
                        try {
                            simconcept.post(new Runnable() {
                                @Override
                                public void run() {

                                   // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

                                      //  am.setTime(sdf.parse(values[0]).getTime());
                                        simconcept.setText(concept);
                                        simTime.setText(values[0]);
                                        simvalue.setText(value);




                                }
                            });

                            Log.i("simulation screen", "simulation - insert data : (" + concept + ", " + value + ", " + values[0]);
                            insertingMeasure(concept, value, values[0]);
                            Thread.sleep(10500);
                            synchronized (this) {
                                while (isPasued) {
                                    try {
                                        wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                        } catch (Exception e) {
                            Log.e("Simlation Screen", "run simulation error. error msg : " + e.getMessage());
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

    // Class that creates the AlertDialog
    public static class AlertDialogFragment extends DialogFragment {

        public static AlertDialogFragment newInstance() {
            return new AlertDialogFragment();
        }

        // Build AlertDialog using AlertDialog.Builder
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage("There are no active projections. please activate/start at least one projection.")

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

    private void startSimulation(View v)
    {
        if(projectionsCollection.getInstance().getCollectionSize()==0)
        // there is no active projections available
        {
            // Create a new AlertDialogFragment
            AlertDialogFragment d=AlertDialogFragment.newInstance();
            d.show(getFragmentManager(), "Alert");
            return;
        }

        switch (prevAction){

            case "init":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        playsim.post(new Runnable() {
                            @Override
                            public void run() {
                                playsim.setImageResource(R.drawable.player_pause);
                                playTxt.setText("Pause Simulation");

                            }
                        });
                    }
                }).start();
                prevAction="play";
                runSimulation();
                simulationThread.start();
                break;

            case "stop":
                if( simulationThread!=null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            playsim.post(new Runnable() {
                                @Override
                                public void run() {
                                    playsim.setImageResource(R.drawable.player_pause);
                                    playTxt.setText("Pause Simulation");

                                }
                            });
                        }
                    }).start();
                    prevAction="play";
                    simulationThread.start();

                }
                break;
            case "play":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        playsim.post(new Runnable() {
                            @Override
                            public void run() {
                                playsim.setImageResource(R.drawable.player_play);
                                playTxt.setText("Resume Simulation");

                            }
                        });
                    }
                }).start();
                prevAction="Pause";

                 pauseSimulation();

                break;
            case "Pause":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        playsim.post(new Runnable() {
                            @Override
                            public void run() {
                                playsim.setImageResource(R.drawable.player_pause);
                                playTxt.setText("Pause Simulation");

                            }
                        });
                    }
                }).start();
                prevAction="Resume";
                resumeSimulation();
                break;

            case "Resume" :
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        playsim.post(new Runnable() {
                            @Override
                            public void run() {
                                playsim.setImageResource(R.drawable.player_play);
                                playTxt.setText("Resume Simulation");

                            }
                        });
                    }
                }).start();
                prevAction="Pause";
                pauseSimulation();
                break;
        }



    }

    private void pauseSimulation()
    {
        isPasued=true;


    }
    private void resumeSimulation()
    {
        isPasued=false;
        new Thread(new Runnable() {
                @Override
                public void run() {
                synchronized (simulationThread)
                {  Log.i("Simulation screen ","Simulation resumed");

                    simulationThread.interrupt();

                }
            }
        }).start();


    }
    private void stopSimulation()
    {
       if(simulationThread!=null) {
           try {
               simulationThread.sleep(200);
               simulationThread.interrupt();
               Log.i("Simulation screen ", "Simulation stopped");
           } catch (InterruptedException e) {

           }
       }else

       {
           AlertDialogFragment d=AlertDialogFragment.newInstance();
           d.show(getFragmentManager(), "Alert");
           return;
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
            Log.e("SendActionToHandler", "error sending msg :"+e.getMessage());
        }


    }


    private void SimulateProjections(View v) {
        if(projectionsCollection.getInstance().getCollectionSize()==0)
        // there is no active projections available
        {
            // Create a new AlertDialogFragment
            AlertDialogFragment d=AlertDialogFragment.newInstance();
            d.show(getFragmentManager(), "Alert");
            return;
        }


    }


    private void insertingMeasure(String concept, String value,String timeStr) {
        //simulate insertion
        Intent i = new Intent(concept);
        count+=2;
        String val=value;
        i.putExtra("concept", concept);
        boolean isNumber=android.text.TextUtils.isDigitsOnly(value);
        if(isNumber)
        {  Log.i("sim","onnly digit");
            int newval=Integer.valueOf(value)+count;
            val=String.valueOf(newval);

        }
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.SSSzzz");
        i.putExtra("time", timeStr);
        i.putExtra("value",val);
        //String newDate=sdf.format(new Date());
      mrs.enterMeasure(val,"2012-03-02T10:00:00.000+0200","Catanuria");
        Log.i("sim","enter to mrs");
        sendBroadcast(i, android.Manifest.permission.VIBRATE);

    }
    private String readLocalFile(String projId) {
        try {
            //=================================
            // read file   data from raw resources
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

    protected class loadSimulationDataTask extends AsyncTask<Void, Void, String> {



        @Override

        protected String doInBackground(Void... params) {

            loadSimulationData();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Simulation screen","finish loading simulation data");
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