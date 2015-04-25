package projections.monitoringObjects;

import android.app.Application;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import example.com.mobidoc.CommunicationLayer.PushNotification;
import example.com.mobidoc.ConfigReader;
import projections.DataItem;
import projections.projection;

    /*
        saves all the data according to the constrains defined in the projection.
        the DataCollection saves *only* the relevant data and not all the data.
        we can defined it more like a local small DB to store and monitor  *only* the relevant data.

        *   the DataCollection obj exists in each Projection obj
     */
public class DataCollection {


    private  int _daysToRemember;
    private Vector<DataItem> data;
    private Vector<String> conceptsToMonitor;

    private Hashtable<String, Vector<valueConstraint>>constraints;

   public DataCollection()
   {
       conceptsToMonitor=new Vector<>();

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

    /*===================================================
    // check if the DataCollection has any ValueConstraints
    */

    public boolean hasValueConstraints()
    {
        return constraints.isEmpty();
    }

   /*===============================================
    add Constraint that check the value to monitor like : concept #1234 need to be  >= from 90 .
    we add all the value Constraint to the data collection so we can check ,
    if any Constraint happened
    ============================================ */

   public void addValueConstraint(String concept,valueConstraint cons)
   {
      if(!conceptsToMonitor.contains(concept)) {
          constraints.put(concept, new Vector<valueConstraint>());
          conceptsToMonitor.add(concept);
      }

      constraints.get(concept).add(cons);

   }

    /*=======================================
    sets the amount of days to remember backward in the collection

    ==========================================*/

    public void setTimeConstraint(int days)
    {
        _daysToRemember=days;
    }

    public Vector<DataItem> getDataItems()
    {
        return data;
    }

    /*=================================================
    get a iterable collection of all the data item
    ================================ */

    public Iterable getDataValues()
    {
        List<Integer> listint = new ArrayList<Integer>();
        List<String> liststr = new ArrayList<String>();
        for (DataItem d : data) {
           // boolean isInt=android.text.TextUtils.isDigitsOnly(d.getVal());
            //if(isInt)
               // listint.add(Integer.parseInt(d.getVal()));
           // else
                liststr.add(d.getVal());
        }

        return liststr;
    }

    /*============================================================
        add an item to the collection and remove all other data that need to
        be removed according to the duration that have been past.
        (i.e if we defined to remember data for 2 days. when a new item comes in we deleted
        all the itmes such that Item.date < newItem.date - 2days)
     ================================================================*/


    public void insertItem(String concept,String val,Date dateNow)
    {
        //add to the collecction only if the concept need to be monitored
        Log.i("DataCollection","inserting dataItem : (concept:"+concept+",value:"+val+",time:"+dateNow.toString());
        if(conceptsToMonitor.contains(concept)) {
            Log.i("DataCollection","the concept : "+concept+"exists in the concepts to monitoring");
            DataItem item = new DataItem(concept, val, dateNow);

            data.add(item);
            Log.i("DataCollection","removing old items");
            Calendar temp=Calendar.getInstance();
            temp.set(2014,2,1,0,0);
            //String  isSimulated=new ConfigReader(null).getProperties().getProperty("openMRS_URL");
            removeOldItems(temp.getTime());
        }
    }

    /*========================================================
        removed old and unnecessary items from the collection
        according to time constraints
     ==========================================================*/

    private void removeOldItems(Date nowTime)
    {
        Calendar c = Calendar.getInstance();

        // convert the date to the correct format
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        c.setTime(nowTime);
        c.add(Calendar.DATE, -_daysToRemember);

        Date TimeAgo=c.getTime();

        Date TimeDaysago=null;
        Date now=null;

        //loop for all the collection and check if every item
        // is between the required time
        for (int i=0;i<data.size();i++)
        {
            DataItem item=data.get(i);
            try {

                TimeDaysago = sdf.parse(sdf.format(TimeAgo));

                //check if the date is satisfy time constraints
                // meaning if the item date is between the time constraints (now , now-daysToRemembers)
                boolean dateMatch=checkDate(item.getItemDate(),TimeDaysago);

                //if the item is less then now-daysToRemembers-> we will remove it from the collection
                if(!dateMatch) {
                    data.remove(i);
                    Log.i("DataCollection","remove item:("+item.getConcept()+","+item.getVal()+","+item.getItemDate().toString());
                }

            } catch (ParseException e) {
                Log.e("DataCollection-","error parsing date");
            }
        }
    }

    /*===============================================
     check if the date is satisfy time constraints
      meaning if the item date is between the time constraints (now , now-daysToRemembers)
     ================================================*/
    private  boolean  checkDate(Date itemDate,Date daysAgoDate)
    {
        if ((itemDate.after(daysAgoDate)))
            return true;

        return  false;

    }

}
