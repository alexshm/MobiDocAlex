package example.com.mobidoc.Screens.popUpScreens;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.R;

/**
 * Created by alex.shmaltsuev on 15/03/2015.
 */
public class MeasurePop extends Activity{

    private OpenMrsApi openMrsApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messurepopscreen);
        String baseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        openMrsApi = new OpenMrsApi(baseUrl);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView t = (TextView) findViewById(R.id.message);
        Bundle extras = getIntent().getExtras();
        String newString = extras.getString("msg");
        t.setText(newString);

    }

    public void done(View view) {
//        EditText answerText=(EditText)findViewById(R.id.answer);
//        String answerString = answerText.getText().toString();
        finish();
    }
}
