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

public class DataCollection {

    private String concept;
    private  int _daysToRemember;
    private Vector<DataItem> data;
    private  valueConstraint constraint;
    public DataCollection(String Concept,int daysToRemember)
    {
        concept=Concept;
        _daysToRemember=daysToRemember;
        data=new Vector<DataItem>();
        constraint=null;
    }
    public DataCollection(String Concept,int daysToRemember,valueConstraint valconst)
    {
        concept=Concept;
        _daysToRemember=daysToRemember;
        data=new Vector<DataItem>();
        constraint=valconst;
    }
   public boolean hasValueConstraint()
   {
       return constraint!=null;
   }
    public void setValueConstraint(valueConstraint cons)
    {
        constraint=cons;
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
       DataItem item=new DataItem(concept,val,dateNow);

        boolean isSatisfy=false;

        if(constraint!=null)
         isSatisfy=constraint.isSatisfyConstraint(item.getVal());

        if(isSatisfy|| constraint==null)
        {
            //removeOldItems(item.getItemDate());

            data.add(item);
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
                now = sdf.parse(sdf.format(nowTime));
                boolean dateMatch=checkDate(item.getItemDate(),TimeDaysago,now);
                if(!dateMatch)
                    data.remove(i);
                i--;
            } catch (ParseException e) {
                Log.e("DataCollection-","error parsing date");
            }


        }

    }

    private  boolean  checkDate(Date itemDate,Date daysAgoDate,Date nowTime)
    {
        if ((itemDate.after(daysAgoDate)) &&(itemDate.before(nowTime)))
            return true;

        return  false;

    }

}
