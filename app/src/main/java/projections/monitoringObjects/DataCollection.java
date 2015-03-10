package projections.monitoringObjects;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import projections.DataItem;
import projections.projection;

public class DataCollection {


    private  int _daysToRemember;
    private Vector<DataItem> data;
    private Vector<String> conceptsToMonitor;

    private Hashtable<String, Vector<valueConstraint>>constraints;

   public DataCollection()
   {
       conceptsToMonitor=new Vector<>();
       //TODO: check what the number for _daysToRemember
       _daysToRemember=1;
       data=new Vector<DataItem>();
      constraints=new Hashtable<>();
   }
    public DataCollection(int daysToRemember)
    {
        conceptsToMonitor=new Vector<>();

        _daysToRemember=daysToRemember;
        data=new Vector<DataItem>();
      constraints=new Hashtable<>();
    }

   public boolean hasValueConstraints()
   {
       return constraints.isEmpty();
   }

    public void addValueConstraint(String concept,valueConstraint cons)
    {
          if(!conceptsToMonitor.contains(concept))

              constraints.put(concept,new Vector<valueConstraint>()) ;

        constraints.get(concept).add(cons);

    }

    public void setTimeConstraint(int days)
    {
        _daysToRemember=days;
    }

    public Vector<DataItem> getDataItems()
    {
        return data;
    }

    public Iterable getDataValues()
    {
        List<Integer> list = new ArrayList<Integer>();
        for (DataItem d : data) {

            list.add(Integer.parseInt(d.getVal()));
        }

        return list;
    }
    public void insertItem(String concept,String val,Date dateNow)
    {
        //add to the collecction only if the concept need to be monitored

        if(conceptsToMonitor.contains(concept)) {
            DataItem item = new DataItem(concept, val, dateNow);

            data.add(item);

            removeOldItems(new Date());
        }
    }

    private void removeOldItems(Date nowTime)
    {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        c.setTime(nowTime);
        c.add(Calendar.DATE, -_daysToRemember);

        Date TimeAgo=c.getTime();

        Date TimeDaysago=null;
        Date now=null;

        for (int i=0;i<data.size();i++)
        {
            DataItem item=data.get(i);
            try {

                TimeDaysago = sdf.parse(sdf.format(TimeAgo));
               // now = sdf.parse(sdf.format(nowTime));
                boolean dateMatch=checkDate(item.getItemDate(),TimeDaysago,nowTime);

                if(!dateMatch)
                    data.remove(i);

            } catch (ParseException e) {
                Log.e("DataCollection-","error parsing date");
            }


        }

    }

    private  boolean  checkDate(Date itemDate,Date daysAgoDate,Date nowTime)
    {
        if ((itemDate.after(daysAgoDate)))
            return true;

        return  false;

    }

}
