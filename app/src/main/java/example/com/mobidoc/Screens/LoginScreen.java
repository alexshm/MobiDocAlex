package example.com.mobidoc.Screens;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.app.Dialog;
import android.app.ProgressDialog;

import example.com.mobidoc.R;
import projections.ScriptingLayer.JsScriptExecutor;


public class LoginScreen extends Activity {
	@SuppressLint("ShowToast")



    private ProgressDialog mProgressDialog;
    private EditText username;
    private EditText  pass;
    private Boolean isAuth;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
        username=(EditText)findViewById(R.id.usernametxt);
        pass=(EditText)findViewById(R.id.passtext);


		Button loginbtn=(Button)findViewById(R.id.loginButton);



		JsScriptExecutor js=new JsScriptExecutor(this.getApplicationContext());
//        js.runScript("");
		//set listener for clicking the button



		loginbtn.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View v) {


               checkLoginInDB(username.getText().toString(), pass.getText().toString());

            }
		});

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
}
