package example.com.mobidoc.Screens.popUpScreens;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.R;

/**
 * Created by Alex on 29/04/2015.
 */
public class YesNoQuestion extends Activity {

    private OpenMrsApi openMrsApi;
    private String accept;
    private String decline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String baseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        openMrsApi = new OpenMrsApi(baseUrl);
        setContentView(R.layout.yesnoquestionpopscreen);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView t = (TextView) findViewById(R.id.questionMessage1);
        Bundle extras = getIntent().getExtras();
        String question = extras.getString("question");
        t.setText(question);
        if (extras.containsKey("ADpopUp")){
            Button yesButton = (Button) findViewById(R.id.yesButton);
            yesButton.setText("Accept");
            Button noButton = (Button) findViewById(R.id.noButton);
            noButton.setText("Decline");
        }
        this.accept = extras.getString("acceptConcept");
        this.decline = extras.getString("declineConcept");
    }

    public void noPressed(View view) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        insertingMeasure(this.decline,"yes",timeStamp);
        finish();
    }

    public void yesPressed(View view) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        insertingMeasure(this.accept,"yes",timeStamp);
        finish();
    }

    private void insertingMeasure(String concept, String value,String timeStr) {
        //simulate insertion
        Intent i = new Intent(concept);
        i.putExtra("concept", concept);

        i.putExtra("time", timeStr);
        i.putExtra("value",value);
        sendBroadcast(i, android.Manifest.permission.VIBRATE);
    }
}
