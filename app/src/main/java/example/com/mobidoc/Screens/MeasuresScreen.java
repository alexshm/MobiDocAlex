package example.com.mobidoc.Screens;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.R;

/**
 * Created by Alex on 22/04/2015.
 */
public class MeasuresScreen extends Activity {
    private OpenMrsApi openMrsApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measuresscreen);
        String baseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        openMrsApi = new OpenMrsApi(baseUrl);
        String[] obs=null;
        try {
             obs = openMrsApi.getObs();
            //        String[] row = { "ROW1", "ROW2", "Row3", "Row4", "Row 5", "Row 6",
//                "Row 7" };
            String[] column = { "#Row","Observation"};
            int rl=obs.length; int cl=column.length;

//        Log.d("--", "R-Lenght--"+rl+"   "+"C-Lenght--"+cl);

            ScrollView sv = new ScrollView(this);
            TableLayout tableLayout = createTableLayout(obs, column,rl, cl);
            HorizontalScrollView hsv = new HorizontalScrollView(this);
            hsv.addView(tableLayout);
            sv.addView(hsv);
            setContentView(sv);
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this, "problem loading content",Toast.LENGTH_SHORT);
            toast.show();
        }


    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private TableLayout createTableLayout(String [] obs, String [] cv,int rowCount, int columnCount) {
        // 1) Create a tableLayout and its params
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        TableLayout tableLayout = new TableLayout(this);
        Drawable res = getResources().getDrawable(R.drawable.health_background);
        tableLayout.setBackground(res);
        TextView messuresText = new TextView(this);
        messuresText.setText("measures");
        messuresText.setTextSize(23);
        messuresText.setTextColor(Color.parseColor("#ff9cffd4"));
        tableLayout.addView(messuresText);
        // 2) create tableRow params
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(1, 1,1, 1);
        tableRowParams.weight = 1;

        for (int i = 0; i < rowCount; i++) {
            // 3) create tableRow
            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundColor(Color.BLACK);
            for (int j= 0; j < columnCount; j++) {
                // 4) create textView
                TextView textView = new TextView(this);
                //  textView.setText(String.valueOf(j));
                textView.setBackgroundColor(Color.WHITE);

                if (i ==0){
                    textView.setText(cv[j]);
                } else if(i>0 && j==0){
                    textView.setText("Row "+i);
                }else {
                    textView.setText(obs[i]);

                }
                textView.setGravity(Gravity.LEFT);
                // 5) add textView to tableRow
                tableRow.addView(textView, tableRowParams);
            }
            tableLayout.setColumnStretchable(i, false);
            // 6) add tableRow to tableLayout
            tableLayout.addView(tableRow, tableLayoutParams);
        }

        return tableLayout;
    }
}


