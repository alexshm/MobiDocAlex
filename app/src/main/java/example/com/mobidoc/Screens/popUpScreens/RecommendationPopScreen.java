package example.com.mobidoc.Screens.popUpScreens;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.R;

/**
 * Created by alex.shmaltsuev on 12/03/2015.
 */
public class RecommendationPopScreen extends Activity {

    private OpenMrsApi openMrsApi;
    private String declineConcept;
    private String acceptConcept;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendationpopscreen);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView t = (TextView) findViewById(R.id.RecommandContent);
        Bundle extras = getIntent().getExtras();
        String baseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        openMrsApi = new OpenMrsApi(baseUrl);
        String recommendation = extras.getString("recommendation");
        t.setText(recommendation);
        Button buttonAccept = (Button) findViewById(R.id.buttonAccept);
        buttonAccept.setText("Accept");
        Button buttonDecline = (Button) findViewById(R.id.buttonDecline);
        buttonDecline.setText("Decline");

        acceptConcept = extras.getString("acceptConcept");
        declineConcept = extras.getString("declineConcept");

    }

    public void DeclinePressed(View view) {
        String timeStamp = sdf.format(new Date());
        insertingMeasure(declineConcept,"no",timeStamp);
        finish();
    }

    public void AcceptPressed(View view) {
        String timeStamp = sdf.format(new Date());
        insertingMeasure(acceptConcept,"yes",timeStamp);
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
