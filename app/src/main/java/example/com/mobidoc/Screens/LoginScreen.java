package example.com.mobidoc.Screens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.concurrent.ExecutionException;

import example.com.mobidoc.CommunicationLayer.PushNotification;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.LoginTask;
import example.com.mobidoc.R;


public class LoginScreen extends Activity {
    @SuppressLint("ShowToast")

    private ProgressDialog mProgressDialog;
    private EditText username;
    private EditText pass;
    private Boolean withOpenMRS;


    //For registration to  Push Notification
    ////////////////////////////////////////////
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "571408319539";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM Demo";
    static final String Registrationid = "";
    static String regid;
    String RegistrationRes;

    GoogleCloudMessaging gcm;
    Context context;

    ///////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        username = (EditText) findViewById(R.id.usernametxt);
        pass = (EditText) findViewById(R.id.passtext);
        withOpenMRS = false;
        context = getApplicationContext();



        Button loginbtn = (Button) findViewById(R.id.loginButton);
        ToggleButton mrsMode = (ToggleButton) findViewById(R.id.openMRSmode);
        //set listener for clicking the button


        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.


        loginbtn.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View v) {

                checkLoginInDB(username.getText().toString(), pass.getText().toString());

            }
        });

        mrsMode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                withOpenMRS = !withOpenMRS;
            }
        });


        PushNotification p = PushNotification.getInstance(this);
        p.registerDevice();
    }


    private void checkLoginInDB(final String user, final String password) {
        final String BaseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");


        showDialog(0);
        final LoginTask loginTask = new LoginTask(BaseUrl, withOpenMRS);
        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    Boolean result = loginTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, user, password).get();
                    mProgressDialog.dismiss();
                    continueLogin(result);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }




    private void continueLogin(final boolean result) {
        boolean ok = result;
        if (ok) {
            Intent mainScreen = new Intent(LoginScreen.this, MainScreen.class);

            startActivity(mainScreen);

        } else {

            pass.post(new Runnable() {
                @Override
                public void run() {
                    TextView error = (TextView) findViewById(R.id.errorlable);
                    error.setText("");
                    username.setText("");
                    pass.setText("");

                    error.setText("you typed wrong login deatails.\n please type again.");
                    error.setTextColor(Color.RED);
                }
            });

        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {

        mProgressDialog = new ProgressDialog(this);
        // Set Dialog message
        mProgressDialog.setMessage("Please Wait..");
        mProgressDialog.setTitle("Verifying login");
        // Dialog will be displayed for an unknown amount of time
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        return mProgressDialog;

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
