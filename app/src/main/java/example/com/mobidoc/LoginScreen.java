package example.com.mobidoc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginScreen extends Activity {
	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		
		final EditText username=(EditText)findViewById(R.id.usernametxt);
		final EditText pass=(EditText)findViewById(R.id.passtext);
		final Button loginbtn=(Button)findViewById(R.id.loginButton);
		
		
		
		//set listener for clicking the button
		loginbtn.setOnClickListener(new OnClickListener(){

			
			@Override
			public void onClick(View v) {
				
				
				EditText usrname= (EditText)v.findViewById(R.id.usernametxt);
				EditText password= (EditText)v.findViewById(R.id.passtext);
				boolean authrize=checkLogin(username.getText(),pass.getText());
				
				//check the login for the user
				//if ok move to the next screen
				if (authrize){
					
					//for now the user name is : admin pass :12345
					
					Intent mainScreen=new Intent(LoginScreen.this,MainScreen.class);
					startActivity(mainScreen);
					
				}
				else
				{
					username.setText("");
					pass.setText("");
					TextView error=(TextView)findViewById(R.id.errorlable);
					error.setText("you typed wrong login deatails.\n please type again." );
					error.setTextColor(Color.RED);
					
					
				}
			}
			
		});
		
		
	}

	
	//check the login cardentails for the user
	public boolean checkLogin(Editable username, Editable password) {
		
		String user=username.toString();
		String pass=password.toString();
		
		System.out.println("user name : "+user);
		
		if (user.equals("admin") && pass.equals("12345"))
			return true;
		
		return false;
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
