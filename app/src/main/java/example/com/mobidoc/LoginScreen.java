package example.com.mobidoc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginScreen extends Activity {
    private EditText usrname;
    private EditText password;


    @SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
        usrname= (EditText)findViewById(R.id.usernametxt);
        password= (EditText)findViewById(R.id.passtext);

	}

	
	//check the login cardentails for the user
	public void checkLogin(View view) {
		System.out.println("user name : "+usrname.getText().toString());

        //user name is admin 12345
		if (usrname.getText().toString().equals("admin") && password.getText().toString().equals("12345")){
            Intent mainScreen=new Intent(LoginScreen.this,MainScreen.class);
            startActivity(mainScreen);
        }
        else{
            usrname.setText("");
            password.setText("");
            TextView error=(TextView)findViewById(R.id.errorlable);
            error.setText("you typed wrong login deatails.\n please type again." );
            error.setTextColor(Color.RED);
        }

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
