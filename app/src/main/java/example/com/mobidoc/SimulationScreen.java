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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import dalvik.system.DexClassLoader;
import javassist.ClassPool;
import projections.Actions.Action;
import projections.Actions.NotificationAction;
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
        //   Toast.makeText(getApplicationContext(), "welcome to MobiDoc", Toast.LENGTH_LONG);

        final Button startbtn = (Button) findViewById(R.id.startSimulation);
        final Button handlerSender = (Button) findViewById(R.id.button2);

        t = (EditText) findViewById(R.id.editText);
        everyXtxt = (EditText) findViewById(R.id.editText);
        remaindertxt = (EditText) findViewById(R.id.editText2);
        startTimetxt = (EditText) findViewById(R.id.editText3);

        final Button con = (Button) findViewById(R.id.button);

        //===================
        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.elements, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        //===============
        // spiner for the remainder combo box
        //===================
        reminder_spinner = (Spinner) findViewById(R.id.spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterReminder = ArrayAdapter.createFromResource(this,
                R.array.reminder_elements, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterReminder.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        reminder_spinner.setAdapter(adapterReminder);
        //===============

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

                if (selectedProjection.contains("Monitor")) {
                    //visible all the start time+frequncy settings for cyclic
                    everyXtxt.setVisibility(View.INVISIBLE);
                    findViewById(R.id.textView2).setVisibility(View.INVISIBLE);
                    findViewById(R.id.textView3).setVisibility(View.INVISIBLE);
                    findViewById(R.id.textView4).setVisibility(View.INVISIBLE);
                    remaindertxt.setVisibility(View.INVISIBLE);
                    reminder_spinner.setVisibility(View.INVISIBLE);
                    startTimetxt.setVisibility(View.INVISIBLE);
                    spinner.setVisibility(View.INVISIBLE);
                } else {
                    everyXtxt.setVisibility(View.VISIBLE);
                    findViewById(R.id.textView2).setVisibility(View.VISIBLE);
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    findViewById(R.id.textView4).setVisibility(View.VISIBLE);
                    remaindertxt.setVisibility(View.VISIBLE);
                    reminder_spinner.setVisibility(View.VISIBLE);
                    startTimetxt.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.VISIBLE);
                }
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

                if (selectedProjection.contains("Monitor") || (!selectedProjection.contains("Monitor") && everyXtxt.getText().toString() != "" && Integer.parseInt(everyXtxt.getText().toString()) > 0))
                    SimulateProjections(v);
                else
                    Toast.makeText(v.getContext(), "You enter in valid number. please enter again", Toast.LENGTH_LONG).show();

            }
        });

        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register to remainder event
                count++;

                Intent i = new Intent("5037");
                i.putExtra("concept", "5037");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
                Date dateNow = new Date();
                String now = sdf.format(dateNow);
                i.putExtra("time", now);

                i.putExtra("value", "yes");
                sendBroadcast(i, android.Manifest.permission.VIBRATE);

            }
        });

    /*
        test t=new test();
        var v=new var();
        System.out.println("the action before is : ");
        v.test();

        System.out.println("the action after is : ");
        t.test();

*/


    }

    private void SendActionToHandler() {
//        Action a = new MeasurementAction("ss", "5088", getApplicationContext());
        Action a = new NotificationAction("Dont forget your pills" ,"5088",Action.Actor.Patient,getApplicationContext());
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
        //projectionsManager mg = new projectionsManager(this.getApplicationContext());

        ProjectionBuilder pb =new ProjectionBuilder(this.getApplicationContext());


         String jsonString=readProjectionTxt(projectionId);
        // projection p=pb.FromJson(jsonString);

        projection p =pb.build(jsonString);
        /*
        CyclicProjectionAbstract proj = new CyclicProjectionAbstract("test", this.getApplicationContext(), "08:00");
        //((CyclicProjectionAbstract)p).setFrequency(projection.ProjectionTimeUnit.Minute,1);
        // ((CyclicProjectionAbstract)p).setReaminder(projection.ProjectionTimeUnit.Second,30);
        ((CyclicProjectionAbstract)proj).setFrequency(Second,40);


        Action m1 = new MeasurementAction("yes=mesure Ketonuria", "5021", this.getApplicationContext());

        Action m2 = new MeasurementAction("no=mesure 2 test", "1234", this.getApplicationContext());

        Action m3 = new QuestionAction("quesstion ask", "5037", this.getApplicationContext());
        ((QuestionAction)m3).addToSuccessAction(m1);
        ((QuestionAction)m3).addToFailAction(m2);

        proj.addAction(m3);
       p=proj;
        */
        if (p != null) {
            Log.i("start projection", "starting projection : " + projectionId);
            p.startProjection();

        }



       /*
            PROJECTION EXAMPLE
            ======================
            int amount = Integer.parseInt(everyXtxt.getText().toString());
            int remider = Integer.parseInt(remaindertxt.getText().toString());
            String startTime = startTimetxt.getText().toString();

            CyclicProjectionAbstract proj = new CyclicProjectionAbstract("test", this.getApplicationContext(), "08:00");
            //((CyclicProjectionAbstract)p).setFrequency(projection.ProjectionTimeUnit.Minute,1);
           // ((CyclicProjectionAbstract)p).setReaminder(projection.ProjectionTimeUnit.Second,30);
            ((CyclicProjectionAbstract)proj).setFrequency(Second,40);


            MeasurementAction m1 = new MeasurementAction("mesure Ketonuria", "5021", this.getApplicationContext());

            MeasurementAction m2 = new MeasurementAction("mesure 2 test", "1234", this.getApplicationContext());

            MeasurementAction m3 = new MeasurementAction("mesure 3 test", "12345", this.getApplicationContext());
            proj.addAction(m1);
           proj.addAction(m2);

            proj.addAction(m3);
            proj.setExectuionMode(Utils.ExecuteMode.Parallel);

        p=proj;

            //TODO: uncomment startTime below
            // ((CyclicProjectionAbstract)p).setStartTime(startTime);
        }

        //start the projection
        //////////////////////

        if (!p.Isbound()) {
            p.startProjection();
        }

        */
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
            Log.e("read Projecction file in", "error reading file: " + projId);
            return null;
        }
    }

    private void loadAndInvokeJar() {

        Class<?>[] params = new Class[]{BlockingQueue.class};
        try {
            final String libPath = Environment.getExternalStorageDirectory() + "/makejar.jar";
            final File tmpDir = getDir("dex", 0);

            final DexClassLoader classloader = new DexClassLoader(libPath, tmpDir.getAbsolutePath(), null, this.getClass().getClassLoader());
            final Class<Object> classToLoad = (Class<Object>) classloader.loadClass("com.example.makejar.example.com.mobidoc.test");


            final Object myInstance = classToLoad.newInstance();
            Method initmeth = classToLoad.getMethod("init", params);
            initmeth.setAccessible(true);
            // final  Thread pp=new Thread((Runnable) myInstance);
            String res = (String) initmeth.invoke(myInstance, new Object[]{q1});
            //Toast.makeText(this.getApplicationContext(),"start the projections.projection Test", 1).show();
            //t.setText("before executing  : "+res);
            Method start = classToLoad.getMethod("start");

            // showToastFromBackground("");
            start.invoke(myInstance);


            // final Method doSomething = classToLoad.getMethod("beepForAnHour");

            //doSomething.invoke(myInstance);

        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "error consumer main : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();


        }
    }


    private void generateDynamicallyDexFileTest() {
        final File tmpDir = getDir("dex", 0);

        final ClassPool cp = ClassPool.getDefault();
        //create class using dexMakerJava assist
        //==============================
        boolean IsToGenerate = false;

        if (IsToGenerate) {
            generateDynamicClass(GENERATE_JAVA_ASSIST);
        }
        //=======================================
        // final String libPath = Environment.getExternalStorageDirectory() + "/HelloDex.dex";
        final String libPath = "";
        final DexClassLoader classloader = new DexClassLoader(libPath, tmpDir.getAbsolutePath(), null, this.getClass().getClassLoader());
        System.out.println("before loading class");

        //Class<CyclicProjection> classToLoad = null;
        try {
            final Class<?> classToLoadw = classloader.loadClass("TestClass1");
            final Object myInstance = classToLoadw.newInstance();
            // Method initmeth = classToLoadw.getMethod("init", params);


            Method start = classToLoadw.getMethod("printTest");
            start.setAccessible(true);
            // showToastFromBackground("");
            int res = (int) start.invoke(myInstance);
            System.out.println("the ans from dynamicaly generated is : " + res);
            int d = 9;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        //  DexMaker dexMaker = new DexMaker();


    }

    // Bind to LoggingService
    @Override
    protected void onResume() {
        super.onResume();
        // Intent serviceIntent = new Intent(this,example.com.mobidoc.MsgRecieverService.class);


        bindService(serviceIntent, mConnection,
                Context.BIND_AUTO_CREATE);

    }

    // Unbind from the LoggingService
    @Override
    protected void onPause() {

        if (mIsBound)
            unbindService(mConnection);

        super.onPause();
    }


    private void generateDynamicClass(int generator) {
        ClassGenerator cg = new ClassGenerator();

        if (generator == GENERATE_WITH_DEXMAKER) {

            cg.generateDex();

        } else {
            ClassPool pool = new ClassPool(true);
            cg.javaAssistGenerator("testfile", this, pool);
        }
    }


}