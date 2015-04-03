package example.com.mobidoc.Screens;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

import example.com.mobidoc.CommunicationLayer.HttpGetTask;
import example.com.mobidoc.CommunicationLayer.HttpRecTask;
import example.com.mobidoc.R;

/**
 * Created by Moshe on 3/25/2015.
 */
public class webComScreen extends Activity {

    private ProgressDialog mProgressDialog;
    private TextView ans;
    private String baseUrl = "http://";
    private PowerManager.WakeLock mWakeLock;
    private String url;
    private EditText ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.communicationscreen);
        ans = (TextView) findViewById(R.id.ansTxt);
        ip = (EditText) findViewById(R.id.editText4);

        Button send = (Button) findViewById(R.id.button3);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                baseUrl = "http://" + ip.getText().toString() + ":8081/openmrs-standalone/ws/rest/v1/";
//                url = baseUrl;
                Log.i("getFromUrl", "connecting to web.. " + url);
//                getFromUrl(url);
                getRecTest();


            }
        });

    }

    private void getRecTest() {
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        final String resultString = getHash.put("URLPath", "session");
        System.out.println("########################");
        System.out.println("Search the persons that have name  JOHN");
        new HttpRecTask("admin", "Admin123", baseUrl) {
            @Override
            public void onPostExecute(String result) {
//                mProgressDialog.dismiss();
                ans.setText(result);

            }
        }.execute(getHash);

    }

    private void getFromUrl(String url) {

        this.showDialog(0);

        new HttpGetTask() {

            @Override
            public void onResponseReceived(String result) {
                mProgressDialog.dismiss();
                ans.setText(result);
///d
            }
        }.execute(url);


    }

    @Override
    protected Dialog onCreateDialog(int id) {

        mProgressDialog = new ProgressDialog(this);
        // Set Dialog message
        mProgressDialog.setMessage("Connecting to " + url);
        mProgressDialog.setTitle("Please Wait while Connecting..");
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
}
