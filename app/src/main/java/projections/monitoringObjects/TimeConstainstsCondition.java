package projections.monitoringObjects;

import org.apache.http.ParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import projections.DataItem;


public class TimeConstainstsCondition {


    private int numDaysAgo;


    public TimeConstainstsCondition(int countDaysAgo) {
        numDaysAgo = countDaysAgo;

        Date dateNow;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String now = sdf.format(new Date());


    }


    public boolean isCondHappend(Date nowTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");


        Calendar c = Calendar.getInstance();


        c.setTime(nowTime);
        c.add(Calendar.DATE, -numDaysAgo);


        String TimeDaysago = sdf.format(c.getTime());

        return false;
    }




}


