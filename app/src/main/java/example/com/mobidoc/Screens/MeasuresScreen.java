package example.com.mobidoc.Screens;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.R;

/**
 * Created by Alex on 22/04/2015.
 */
public class MeasuresScreen extends Activity {
    private OpenMrsApi openMrsApi;
    private ProgressDialog startGLDialog;
    private String[] obs;
    private TextView t;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measuresscreen);
        t=(TextView)findViewById(R.id.textView18);
        obs=null;

        showDialog(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   obs=new downloadObsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR ).get();
                    startGLDialog.dismiss();
                    String[] column = { "#Row","Observation"};
                    int rl=obs.length; int cl=column.length;
                    final ScrollView sv = new ScrollView(getApplicationContext());
                    TableLayout tableLayout = createTableLayout(obs, column,rl, cl);

                    HorizontalScrollView hsv = new HorizontalScrollView(getApplicationContext());
                    hsv.addView(tableLayout);

                    sv.addView(hsv);
                    t.post(new Runnable() {
                         @Override
                         public void run() {
                            MeasuresScreen.this.setContentView(sv);
                         }
                     });


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                catch (Exception e){
                    Log.i("aaa", "error: " + e.getMessage());
                }

            }
        }).start();



    }


    @Override
    protected Dialog onCreateDialog(int id) {

        startGLDialog = new ProgressDialog(this);
        // Set Dialog message
        startGLDialog.setMessage("Please Wait for Collecting Data..");
        startGLDialog.setTitle("Getting Patient Data...");

        // Dialog will be displayed for an unknown amount of time
        startGLDialog.setIndeterminate(true);
        startGLDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        startGLDialog.setCancelable(false);
        startGLDialog.show();
        return startGLDialog;

    }
private class downloadObsAsync extends AsyncTask<String, Void, String[]> {


    @Override
    protected String[] doInBackground(String... params) {
        String baseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        openMrsApi = new OpenMrsApi(baseUrl);
        String [] ans = openMrsApi.getObs();

        return ans;
    }
}
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private TableLayout createTableLayout(String [] obs, String [] cv,int rowCount, int columnCount) {
        // 1) Create a tableLayout and its params
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        TableLayout tableLayout = new TableLayout(getApplicationContext());
        Drawable res = getResources().getDrawable(R.drawable.health_background);
        tableLayout.setBackground(res);
        TextView messuresText = new TextView(getApplicationContext());

        messuresText.setText("measures");
        messuresText.setTextSize(18);
        messuresText.setTextColor(Color.parseColor("#ff9cffd4"));
        tableLayout.addView(messuresText);
        // 2) create tableRow params
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(0, 0,0, 0);
        tableRowParams.weight = 1;
        Drawable drawable= null;
        for (int i = 0; i < rowCount; i++) {
            // 3) create tableRow
            TableRow tableRow = new TableRow(this);
            for (int j= 0; j < columnCount; j++) {
                // 4) create textView
                TextView textView = new TextView(this);

                textView.setPadding(5,0,0,0);
                textView.setGravity(Gravity.CENTER);
                TableLayout.LayoutParams p=new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.weight=1;
                 p.width=1;
                textView.setLayoutParams(p);


                if (i ==0){
                    textView.setText(cv[j]);
                    drawable =  getResources().getDrawable(R.drawable.title_shape);
                    textView.setBackground(drawable);
                    textView.setWidth(0);
                } else if(i>0 && j==0){
                    textView.setText("Row "+i);
                    drawable =  getResources().getDrawable(R.drawable.title_shape);
                    textView.setBackground(drawable);
                }else {
                    drawable =  getResources().getDrawable(R.drawable.cell_shape);
                    textView.setText(obs[i]);
                    textView.setBackground(drawable);

                }

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


