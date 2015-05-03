package example.com.mobidoc.Screens.popUpScreens;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import example.com.mobidoc.R;

/**
 * Created by alex.shmaltsuev on 12/03/2015.
 */
public class PopScreen extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popscreen);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView t = (TextView) findViewById(R.id.popUpMessage);
        Bundle extras = getIntent().getExtras();
        String newString = extras.getString("msg");
        t.setText(newString);
    }

    public void close(View view) {
        finish();
    }
}



