package example.com.mobidoc.Screens;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.R;
import example.com.mobidoc.Screens.popUpScreens.MeasurePop;

/**
 * Created by Alex on 22/04/2015.
 */
public class MeasuresScreen extends Activity {
    private OpenMrsApi openMrsApi;
    private ProgressDialog startGLDialog;
    private String[][] obs;
    private TextView t;
    private Button manuallyBG;
    private Button manuallyBP;
    private Button manuallyket;
    private LinearLayout lay;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measuresscreen);
         t=(TextView)findViewById(R.id.textView7);
        obs=null;

        showDialog(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   obs=new downloadObsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR ).get();
                    startGLDialog.dismiss();
                    String[] column = { "","Type","Date","Value"};
                    int rl=obs.length; int cl=column.length;

                    final TableLayout tableLayout = createTableLayout(obs, column,rl, cl);
                    final ScrollView sv=new ScrollView(MeasuresScreen.this);
                    final  HorizontalScrollView hsv=new HorizontalScrollView(MeasuresScreen.this);
                    hsv.addView(tableLayout);

                    final LinearLayout l=new LinearLayout(MeasuresScreen.this);
                    l.setOrientation(LinearLayout.VERTICAL);

                    final  LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);



                    t.post(new Runnable() {
                         @Override
                         public void run() {

                              View v = vi.inflate(R.layout.measuresscreen, null);

                             // fill in any details dynamically here
                             LinearLayout old = (LinearLayout) v.findViewById(R.id.measurelayout);

                             Button insertbg = (Button) v.findViewById(R.id.insertbg);

                             Button insertbp = (Button) v.findViewById(R.id.insertbp);

                             Button insertket = (Button) v.findViewById(R.id.insertketanuria);


                             setNewClickListener(insertbg);
                             setNewClickListener(insertbp);
                             setNewClickListener(insertket);

                             sv.addView(hsv);

                             old.addView(sv,1);

                             MeasuresScreen.this.setContentView(v);


                         }
                     });


                } catch (InterruptedException e) {

                    Log.e("Measures Screen","an error has occurred while getting information from Open MRS. error msg : "+e.getMessage());
                    String alertMsg="an error has occurred while getting information from Open MRS.try again later.";
                    AlertDialogFragment d=AlertDialogFragment.newInstance(alertMsg);
                    d.show(getFragmentManager(), "Alert");
                } catch (ExecutionException e) {
                    Log.e("Measures Screen","an error has occurred while getting information from Open MRS. error msg : "+e.getMessage());
                    String alertMsg="an error has occurred while getting information from Open MRS.try again later.";
                    AlertDialogFragment d=AlertDialogFragment.newInstance(alertMsg);
                    d.show(getFragmentManager(), "Alert");
                }
                catch (Exception e){
                    Log.e("Measures Screen","an error has occurred while getting information from Open MRS. error msg : "+e.getMessage());
                    String alertMsg="an error has occurred while getting information from Open MRS.try again later.";
                    AlertDialogFragment d=AlertDialogFragment.newInstance(alertMsg);
                    d.show(getFragmentManager(), "Alert");
                }

            }
        }).start();





    }

    // Class that creates the AlertDialog
    public static class AlertDialogFragment extends DialogFragment {
        private static String msg;


        public static AlertDialogFragment newInstance(String _msg) {
            msg=_msg;
            return new AlertDialogFragment();
        }

        // Build AlertDialog using AlertDialog.Builder
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(msg)

                            // User cannot dismiss dialog by hitting back button
                    .setCancelable(false)
                            // Set up ok Button
                    .setNeutralButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
        }
    }



    private void setNewClickListener(View v) {

        new clickListener(v);

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

    private void insertMeasureManually(final String measureType,final String measureConcept)
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String txt;
                txt = measureType;
                String concept = measureConcept;
                Intent intent = new Intent(MeasuresScreen.this, MeasurePop.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("msg", txt);
                intent.putExtra("concept", concept);
                Log.i("testtt","sending concept : "+measureConcept);
                Log.i("testtt","sending text : "+measureType);
                getApplicationContext().startActivity(intent);
            }
        }).start();


    }

    private class clickListener implements View.OnClickListener
    {
      private String name="";
        private String concept="";

        public clickListener(View v)
        {

            Button bv=(Button)v;
            bv.getText();

            String text= bv.getText().toString().split(" ")[1];

            switch (text)
            {
                case "BP":
                    concept="5178";
                    break;
                case "BG":
                    concept="4985";
                    break;
                case "ketonuria":
                    concept="5021";
                    break;

            }
            v.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {

            insertMeasureManually(this.name, this.concept);
        }
    }


private class downloadObsAsync extends AsyncTask<String, Void, String[][]> {


    @Override
    protected String[][] doInBackground(String... params) {
        String baseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        openMrsApi = new OpenMrsApi(baseUrl);
        String [][] ans = openMrsApi.getObs();

        return ans;
    }
}
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private LinearLayout createNewLayout()
    {
        LinearLayout linerl=new LinearLayout(MeasuresScreen.this);
        String[] column = { "#Num","Type","Date","Value"};

        int rl=obs.length; int cl=column.length;

        TableLayout tableLayout = createTableLayout(obs, column,rl, cl);

        ScrollView sv=new ScrollView(MeasuresScreen.this);
        HorizontalScrollView hsv=new HorizontalScrollView(MeasuresScreen.this);
        hsv.addView(tableLayout);


        linerl.setOrientation(LinearLayout.VERTICAL);
        sv.addView(hsv);
        linerl.addView(sv);
        return linerl;

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private TableLayout createTableLayout(String [][] obs, String [] cv,int rowCount, int columnCount) {
        // 1) Create a tableLayout and its params
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        TableLayout tableLayout = new TableLayout(MeasuresScreen.this);


        // 2) create tableRow params
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(0, 0,0, 0);
        tableRowParams.weight = 1;
        Drawable drawable= null;
        for (int i = 0; i < rowCount; i++) {
            // 3) create tableRow
            TableRow tableRow = new TableRow(MeasuresScreen.this);
            for (int j= 0; j < columnCount; j++) {
                // 4) create textView
                TextView textView = new TextView(MeasuresScreen.this);

                textView.setPadding(5,0,0,0);
                textView.setGravity(Gravity.CENTER);
                TableLayout.LayoutParams p=new TableLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.weight=1;
                 p.width=1;
                textView.setLayoutParams(p);


                if (i ==0){
                    textView.setText(cv[j]);
                    drawable =  getResources().getDrawable(R.drawable.title_shape);
                    textView.setBackground(drawable);
                    textView.setWidth(0);
                } else if(i>0 && j==0){
                    textView.setText(i+" ) ");
                    drawable =  getResources().getDrawable(R.drawable.title_shape);
                    textView.setBackground(drawable);
                }else {
                    drawable =  getResources().getDrawable(R.drawable.cell_shape);
                    textView.setText("  "+obs[i][j-1]+"  ");
                    textView.setBackground(drawable);

                }

                // 5) add textView to tableRow
                tableRow.addView(textView, tableRowParams);
            }
            tableLayout.setColumnStretchable(i, true);
            // 6) add tableRow to tableLayout
            tableLayout.addView(tableRow, tableLayoutParams);
        }

        return tableLayout;
    }
}




