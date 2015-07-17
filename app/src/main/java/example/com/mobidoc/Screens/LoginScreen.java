package example.com.mobidoc.Screens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import example.com.mobidoc.CommunicationLayer.PushNotification;
import example.com.mobidoc.CommunicationLayer.ServicesToBeDSS.PicardCommunicationLayer;
import example.com.mobidoc.CommunicationLayer.ServicesToBeDSS.StartGLTask;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.LoginTask;
import example.com.mobidoc.R;


public class LoginScreen extends Activity {
    @SuppressLint("ShowToast")

    private ProgressDialog mProgressDialog;
    private EditText username;
    private EditText pass;

    ///////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        username = (EditText) findViewById(R.id.usernametxt);
        pass = (EditText) findViewById(R.id.passtext);

        Button loginbtn = (Button) findViewById(R.id.loginButton);


        loginbtn.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View v) {

                checkLoginInDB(username.getText().toString(), pass.getText().toString());

            }
        });


    }


    private void checkLoginInDB(final String user, final String password) {
        final String BaseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");


        showDialog(0);
        final LoginTask loginTask = new LoginTask(BaseUrl);
        new Thread(new Runnable() {
            @Override
            public void run() {


                try {

                    Boolean result = loginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user, password).get();

                    mProgressDialog.dismiss();
                    continueLogin(result);
                } catch (InterruptedException e) {
                    mProgressDialog.dismiss();
                    Log.e("Login Screen","an error has occurred while getting information from Open MRS. error msg : "+e.getMessage());
                    String alertMsg="an error has occurred while getting information from Open MRS";
                    AlertDialogFragment d=AlertDialogFragment.newInstance(alertMsg);
                    d.show(getFragmentManager(), "Alert");
                } catch (ExecutionException e) {
                    mProgressDialog.dismiss();
                    Log.e("Login Screen","an error has occurred while getting information from Open MRS. error msg : "+e.getMessage());
                    String alertMsg="an error has occurred while getting information from Open MRS";
                    AlertDialogFragment d=AlertDialogFragment.newInstance(alertMsg);
                    d.show(getFragmentManager(), "Alert");
                }


            }
        }).start();
    }

    private void continueLogin(final boolean result) {
        boolean ok = result;
        if (ok) {
            Intent mainScreen = new Intent(LoginScreen.this, MainScreen.class);
            finish();
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
                            // Set up ok Button
                    .setNeutralButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
        }
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
