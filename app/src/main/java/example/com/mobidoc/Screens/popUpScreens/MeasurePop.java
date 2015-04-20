package example.com.mobidoc.Screens.popUpScreens;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

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
        setCurrentTimeOnView();

    }

    public void done(View view) {

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
