package example.com.mobidoc.Screens.popUpScreens;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.R;

/**
 * Created by alex.shmaltsuev on 15/03/2015.
 */
public class MeasurePop extends Activity {

    private OpenMrsApi openMrsApi;
    private TimePicker timePicker;
    static final int TIME_DIALOG_ID = 999;
    private int hour;
    private int minute;
    private TextView displayTime;
    private EditText value;
    private String concept;
    HashMap<String,String> conceptHash= new HashMap<String,String>();
    private   ImageButton exit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messurepopscreen);
         exit=(ImageButton) findViewById(R.id.exitBtton);

        String baseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        openMrsApi = new OpenMrsApi(baseUrl);
        initHash();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView t = (TextView) findViewById(R.id.message);
        Bundle extras = getIntent().getExtras();
        String msg = extras.getString("msg");
        concept = extras.getString("concept");
        t.setText(msg);
        setCurrentTimeOnView();
        value = (EditText) findViewById(R.id.value);
        value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                value.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) MeasurePop.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(value, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });


    }

    private void initHash() {

        this.conceptHash.put("5178","systolic");
        this.conceptHash.put("4985","GLOUCISE");
        this.conceptHash.put("5021","Ketonuria");
    }

    public void done(View view) {
        String time = displayTime.getText().toString();
        DatePicker datePcker = (DatePicker) findViewById(R.id.datePicker);
        String val = value.getText().toString();
        Date date = (Date) new Date
                (datePcker.getYear() - 1900, datePcker.getMonth(), datePcker.getDayOfMonth());
        date.setHours(hour);

        date.setMinutes(minute);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") {
            public StringBuffer format(Date date, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                StringBuffer toFix = super.format(date, toAppendTo, pos);
                return toFix.insert(toFix.length() - 2, ':');
            }

            ;
        };

        String dateString = dateFormat.format(date);
       String ans = openMrsApi.enterMeasure(val, dateString, this.conceptHash.get(concept));

        //TODO: THIS IS THE OLD ONE   String timeStamp = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss").format(Calendar.getInstance().getTime());

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);

        insertingMeasure(concept,val,timeStamp);
        finish();
    }

    public void setCurrentTimeOnView() {

        displayTime = (TextView) findViewById(R.id.timePickedText);
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        // set current time into textview
        displayTime.setText(
                new StringBuilder().append(pad(hour))
                        .append(":").append(pad(minute)));

        // set current time into timepicker
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public void exit(View v)
    {
        finish();
    }
    public void changeTime(View view) {
        showDialog(TIME_DIALOG_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                // set time picker as current time
                return new TimePickerDialog(this,
                        timePickerListener, hour, minute, false);

        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;

                    // set current time into textview
                    displayTime.setText(new StringBuilder().append(pad(hour))
                            .append(":").append(pad(minute)));

                    // set current time into timepicker
                    timePicker.setCurrentHour(hour);
                    timePicker.setCurrentMinute(minute);

                }
            };

    private void insertingMeasure(String concept, String value,String timeStr) {
        //simulate insertion
        Log.i("measurePop","sending measure to projection with concept : "+concept);
        Intent i = new Intent(concept);
        i.putExtra("concept", concept);

        i.putExtra("time", timeStr);
        i.putExtra("value",value);
        sendBroadcast(i, android.Manifest.permission.VIBRATE);
    }
}


