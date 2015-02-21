package projections.monitoringObjects;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import projections.DataItem;

import static projections.monitoringObjects.monitorTriggerCondElement.*;


public class TriggerCond {
    protected Hashtable<String,DataItem> data;

    private Vector<monitorTriggerCondElement>conditionList;

    public TriggerCond()
    {
            conditionList=new Vector<monitorTriggerCondElement>();
            data=new Hashtable<String,DataItem>() ;

    }

    public void addData(String concept,DataItem dataItem)
    {
        DataItem item=new DataItem(dataItem);
        data.put(concept,item);
    }


    public void  addCondition(String triggerName, ConditionActions action, MonitorOperators op, int val)
    {
        monitorTriggerCondElement condition=new monitorTriggerCondElement(triggerName,action,op,val);
        conditionList.add(condition);
    }

    public Hashtable<String,DataItem> selectDataByTimeConstainsts(int DaysAgo,Date nowTime)
    {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        c.setTime(nowTime);
        c.add(Calendar.DATE, -DaysAgo);

        Date TimeAgo=c.getTime();

        Date TimeDaysago=null;
        Date now=null;

        try {

             TimeDaysago = sdf.parse(sdf.format(TimeAgo));
             now = sdf.parse(sdf.format(nowTime));

        } catch (ParseException e) {
            e.printStackTrace();
        }


        Hashtable filteredData=new Hashtable<String,DataItem>() ;

        synchronized (data)
        {

            Iterator it=data.entrySet().iterator();

            while (it.hasNext())
            {
                Map.Entry<String,DataItem> item=( Map.Entry<String,DataItem>)it.next();
                try {

                    Date itemdate = sdf.parse(sdf.format(item.getValue().getItemDate()));
                    boolean dateMatch=checkDate(itemdate,TimeDaysago,now);

                    if(dateMatch)
                        filteredData.put(item.getKey(),item.getValue());


                } catch (ParseException e) {
                    Log.e("TriggerCond - selectDataByTimeConstainsts","error parsing date : "+item.getValue().getItemDate());
                }

            }
        }


        return null;
    }

    public boolean  checkDate(Date itemDate,Date daysAgoDate,Date nowTime)
    {
        if ((itemDate.after(daysAgoDate)) &&(itemDate.before(nowTime)))
            return true;

        return  false;

    }

    public boolean IsCondHappend(Date now)

    {
        Hashtable<String,DataItem> filtered=selectDataByTimeConstainsts(4, now);

        boolean ans=true;
        for (monitorTriggerCondElement cond:conditionList)
        {
            ans=ans&&cond.isCondHappend();
        }

        return  ans;
    }


}
