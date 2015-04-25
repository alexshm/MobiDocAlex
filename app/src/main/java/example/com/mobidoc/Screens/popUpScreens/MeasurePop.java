package example.com.mobidoc.Screens.popUpScreens;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    private String measureType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messurepopscreen);
        String baseUrl = new ConfigReader(getApplicationContext()).getProperties().getProperty("openMRS_URL");
        openMrsApi = new OpenMrsApi(baseUrl);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView t = (TextView) findViewById(R.id.message);
        Bundle extras = getIntent().getExtras();
        String msg = extras.getString("msg");
        measureType = extras.getString("measureType");
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

    public void done(View view) {
        String time = displayTime.getText().toString();
        DatePicker datePcker = (DatePicker) findViewById(R.id.datePicker);
        int intValue = Integer.parseInt(value.getText().toString());
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
        String ans = openMrsApi.enterMeasure(intValue, dateString, measureType);
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
}
