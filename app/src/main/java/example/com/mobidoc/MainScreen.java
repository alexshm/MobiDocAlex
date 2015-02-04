package example.com.mobidoc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.annotation.SuppressLint;
import android.app.*;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import projections.*;
@SuppressLint("ShowToast")
public class MainScreen extends Activity {
    final BlockingQueue<String> q1 = new ArrayBlockingQueue<String>(1000);
    private TextView t;
   Messenger mMsg=null;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     //   Toast.makeText(getApplicationContext(), "welcome to MobiDoc", Toast.LENGTH_LONG);
        //final EditText username=(EditText)findViewById(R.id.usernametxt);
        //final EditText pass=(EditText)findViewById(R.id.passtext);
        //final Button loginbtn=(Button)findViewById(R.id.loginButton);

        final Button startbtn=(Button)findViewById(R.id.button1);
        final Button sendbrd=(Button)findViewById(R.id.button2);
        final Button sendEvery=(Button)findViewById(R.id.button3);

        t = (TextView) findViewById(R.id.textView2);
        Class<?>[] params = new Class[]{BlockingQueue.class};

        final CyclicProjection cyc1= new CyclicProjection("test Cyclic", this) {

        };

        final CyclicProjection cyc2= new CyclicProjection("test notification", this) {
            @Override
            public void doAction() {
                MeasurementAction m2=new MeasurementAction("other BP","687",this.context);
            }
        };

        final projection p=null;
       //p.SetIntent(MainScreen.this.getApplicationContext());
       // String ac=a.getActionToSend().getData().getString("value");
      //  System.out.println("the string is : "+ac);
        //t.setText(ac);

        startbtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (p.Isbound())
                {

                    //p.InvokeAction(a);

                }
                else
                {

                    p.startProjection();

                }
            }});

        sendbrd.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
               // a.SubscribeConcept("5021");
                Intent i=new Intent("5021");
                i.putExtra("concept","5021");
                String val=String.valueOf(((Math.random()*60)+15));
                i.putExtra("value",val);
                sendBroadcast(i,android.Manifest.permission.VIBRATE);
            }

        });

        sendEvery.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (cyc1.Isbound())
                {
                    //cyc1.startCyclic(projection.ProjectionTimeUnit.Second,40);

                    //p.SetDoActionEvery(projection.ProjectionTimeUnit.Minute,1);
                    //p.StartProjecction_alarm();
                    sendEvery.setEnabled(false);
                }
                else
                {
                    cyc1.makeTestCyclic();
                    cyc1.startCyclic(projection.ProjectionTimeUnit.Second,40);

                    //p.SetDoActionEvery(projection.ProjectionTimeUnit.Minute,1);
                    //p.StartProjecction_alarm();
                    sendEvery.setEnabled(false);
                   // p.startProjection();

                }
            }});
        /*
        Toast.makeText(this.getApplicationContext(), "projections.projection is set every 30 sec", Toast.LENGTH_LONG).show();

        // inst.init(q1);
        // Thread qu=new Thread(inst);

        //Toast.makeText(this.getApplicationContext(),"welocme", 1).show();


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
*/



                /*
                Action a = new Action(Action.ActionType.Measurement, "tttt");

                Message msg = a.getActionToSend();

                ServiceConnection mConnection = new ServiceConnection() {

                    public void onServiceConnected(ComponentName className, IBinder service) {

                        // Messenger object connected to the LoggingService
                        MainScreen.service = new Messenger(service);

                        mIsBound = true;

                    }

                    public void onServiceDisconnected(ComponentName className) {

                        service = null;

                        mIsBound = false;

                    }
                };



                    try {

                        // Send Message to LoggingService using Messenger
                        service.send(msg);

                    } catch (RemoteException e) {
                        Log.e("PROJECTION-ERROR", e.toString());
                    }

               //Intent intent = new Intent(this, MsgRecieverService.class);
                //startService(intent);
            }
            */
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