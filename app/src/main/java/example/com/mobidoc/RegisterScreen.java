package example.com.mobidoc;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Alex on 20/02/2015.
 */
public class RegisterScreen extends Activity {

    private EditText usrname;
    private EditText password;
    private EditText passwordRe;
    private TextView errorTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);
        usrname= (EditText)findViewById(R.id.usernametxt);
        password= (EditText)findViewById(R.id.passtext);
        passwordRe = (EditText)findViewById(R.id.passtext2);
        errorTextView=(TextView)findViewById(R.id.errorlable);
    }

    public void registerClick(View view) {
        String userNameString = usrname.getText().toString();
        String passwordString = password.getText().toString();
        String passwordReString = passwordRe.getText().toString();
        if(EmptyStrings(userNameString, passwordReString, passwordReString)){
            errorTextView.setText("Please fill in all the fields" );
            errorTextView.setTextColor(Color.RED);
        }
        else if (!passwordString.equals(passwordReString)){
            errorTextView.setText("The password dont match" );
            errorTextView.setTextColor(Color.RED);
        }
        //else all is good
        else{
            registerUser(userNameString,passwordString);
        }

    }

    //TODO this method need to register a user in the database in the server
    private void registerUser(String userNameString, String passwordString) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_screen, menu);
        return true;
    }



    private boolean EmptyStrings(String userNameString, String passwordReString, String reString) {
        return userNameString.equals("") || passwordReString.equals("") ||reString.equals("");
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
