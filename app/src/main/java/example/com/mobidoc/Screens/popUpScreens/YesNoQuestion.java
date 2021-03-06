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
import java.util.Date;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.R;

/**
 * Created by Alex on 29/04/2015.
 */
public class YesNoQuestion extends Activity {

    private OpenMrsApi openMrsApi;
    private String noConcept;
    private String yesConcept;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.yesnoquestionpopscreen);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView t = (TextView) findViewById(R.id.questionMessage1);
        Bundle extras = getIntent().getExtras();
        String baseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        openMrsApi = new OpenMrsApi(baseUrl);

        String question = extras.getString("question");
        t.setText(question);
        Button yesButton = (Button) findViewById(R.id.yesButton);
        yesButton.setText("Yes");
        Button noButton = (Button) findViewById(R.id.noButton);
        noButton.setText("No");

       yesConcept = extras.getString("yesConcept");
        noConcept = extras.getString("noConcept");
    }

    public void noPressed(View view) {
        String timeStamp = sdf.format(new Date());
        insertingMeasure(noConcept,"no",timeStamp);
        finish();
    }

    public void yesPressed(View view) {
        String timeStamp = sdf.format(new Date());
        insertingMeasure(yesConcept,"yes",timeStamp);
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
