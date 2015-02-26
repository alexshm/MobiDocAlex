package example.com.mobidoc;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.annotation.SuppressLint;
import android.app.*;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import dalvik.system.DexClassLoader;
import javassist.ClassPool;
import projections.*;
import projections.mobiDocProjections.projectionsManager;

import static projections.projection.ProjectionTimeUnit.*;

@SuppressLint("ShowToast")
public class SimulationScreen extends Activity {
    static final int GENERATE_WITH_DEXMAKER = 1;
    static final int GENERATE_JAVA_ASSIST = 2;
    final BlockingQueue<String> q1 = new ArrayBlockingQueue<String>(1000);
    private EditText t;
    private  RadioButton katProj;
    private  RadioButton bgProj;
    private  RadioButton monitorProj;
    Messenger mMsg=null;
    private EditText everyXtxt;
    private EditText remaindertxt;
    private EditText startTimetxt;
    private Spinner spinner;
    private Spinner reminder_spinner;
    private    int count=1;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simulation_screen);

        //   Toast.makeText(getApplicationContext(), "welcome to MobiDoc", Toast.LENGTH_LONG);

        final Button startbtn = (Button) findViewById(R.id.startSimulation);

        katProj = (RadioButton) findViewById(R.id.radioButton);
        bgProj = (RadioButton) findViewById(R.id.radioButton2);
        monitorProj = (RadioButton) findViewById(R.id.radioButton3);
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

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(everyXtxt.getText().toString()!="" &&  Integer.parseInt(everyXtxt.getText().toString())>0)
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
                IntentFilter RemainderintentFilter = new IntentFilter("ketanuria");
                Intent i=new Intent("5021");
                i.putExtra("concept","5021");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
                Date dateNow=new Date();
                String now = sdf.format(dateNow);
                i.putExtra("time",now);
                if(count>6|| count<3)
                i.putExtra("value",String.valueOf((84+count)));
                else
                i.putExtra("value", String.valueOf((100 + count)));

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

    private void SimulateProjections(View v) {
        projectionsManager mg = new projectionsManager(this.getApplicationContext());

        CyclicProjectionAbstract proj;
        MonitorProjection monitor_Proj = new MonitorProjection("ketanuriaTestProj", this.getApplicationContext());

        if (katProj.isChecked()) {
            proj = (CyclicProjectionAbstract) mg.getprojection(0);

        } else
            proj = (CyclicProjectionAbstract) mg.getprojection(1);


        int amount = Integer.parseInt(everyXtxt.getText().toString());
        int remider = Integer.parseInt(remaindertxt.getText().toString());
        String startTime = startTimetxt.getText().toString();

        //for monitor projection
        //////////////////////
        if (monitorProj.isChecked()) {
            if (monitor_Proj.Isbound()) {

                v.setEnabled(false);
            } else {
                monitor_Proj.startMonitor();

                v.setEnabled(false);
            }
        }
        //for Cyclic projection
        //////////////////////
        else {

            if (proj.Isbound()) {

                v.setEnabled(false);
            } else {
                proj.startCyclic(Minute, 1, None, 35);

                v.setEnabled(false);


            }
        }
        /*
        Enumeration it=mg.getAllProjections();
            while (it.hasMoreElements())

            {
                CyclicProjectionAbstract proj=(CyclicProjectionAbstract)it.nextElement();
                if (proj.Isbound()) {

                    v.setEnabled(false);
                } else {
                    proj.startCyclic(Second, 40,);

                    v.setEnabled(false);


                }
            }
            */

    }

    private void loadAndInvokeJar(){

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



    private void generateDynamicallyDexFileTest()
    {
        final File tmpDir = getDir("dex", 0);

        final ClassPool cp = ClassPool.getDefault();
        //create class using dexMakerJava assist
        //==============================
        boolean IsToGenerate=false;

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
            final Class<?> classToLoadw =  classloader.loadClass("TestClass1");
            final Object myInstance = classToLoadw.newInstance();
            // Method initmeth = classToLoadw.getMethod("init", params);


            Method start = classToLoadw.getMethod("printTest");
            start.setAccessible(true);
            // showToastFromBackground("");
            int res = (int) start.invoke(myInstance);
            System.out.println("the ans from dynamicaly generated is : "+res);
            int d=9;
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



    private void generateDynamicClass(int generator)
    {
        ClassGenerator cg=new ClassGenerator();

        if (generator==GENERATE_WITH_DEXMAKER)
        {

            cg.generateDex();

        }
        else
        {
            ClassPool pool=new ClassPool(true);
            cg.javaAssistGenerator("testfile",this,pool);
        }
    }





}