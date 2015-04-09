package example.com.mobidoc.Screens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Dialog;
import android.app.ProgressDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.NavigableMap;

import example.com.mobidoc.CommunicationLayer.PushNotification;
import example.com.mobidoc.CommunicationLayer.pushNotificationServices.DemoActivity;
import example.com.mobidoc.R;
import projections.Actions.compositeAction;
import projections.ScriptingLayer.JsScriptExecutor;
import projections.var;


public class LoginScreen extends Activity {
	@SuppressLint("ShowToast")

    private ProgressDialog mProgressDialog;
    private EditText username;
    private EditText  pass;
    private Boolean isAuth;

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

        PushNotification p=PushNotification.getInstance(getApplicationContext());
p.registerDevice();

        Button loginbtn = (Button) findViewById(R.id.loginButton);

        //set listener for clicking the button

        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.



		loginbtn.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View v) {
               // PushNotification p=PushNotification.getInstance();



               checkLoginInDB(username.getText().toString(), pass.getText().toString());

            }
		});

    }


    private void registerDevice() {
        if (checkPlayServices()) {

            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }

        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    private void registerInBackground() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {

        final SharedPreferences prefs = getGcmPreferences(context);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        System.out.println("the device registration is : "+registrationId);
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }
        /**
         * @return Application's {@code SharedPreferences}.
         */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(DemoActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }

        return true;
    }




    private void checkLoginInDB(String user,String password) {

        new DownloadFileAsync().execute(user,password);
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
    class DownloadFileAsync extends AsyncTask<String, Boolean, Boolean> {

       @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(0);
        }

        @Override

        protected Boolean doInBackground(String... params) {
            int count;

            try {
                Thread.sleep(1500);
                Boolean ans = CheckLoginDialog(params[0], params[1]);
                return ans;

            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }

        // Class that creates the ProgressDialog

        private boolean CheckLoginDialog(String user, String pass) {
            //System.out.println("user name : "+user);

            if (user.equals("admin") && pass.equals("12345"))
                return true;

            return false;
        }

        private void continueLogin(final Boolean result) {
            if (result) {
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
        protected void onPostExecute(Boolean result) {
            mProgressDialog.dismiss();
            continueLogin(result);

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
            // Check device for Play Services APK.
            checkPlayServices();
        }
}
