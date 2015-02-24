package projections;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import projections.monitoringObjects.DataCollection;

import  projections.monitoringObjects.*;


public class TriggerAction extends Action {
    protected Vector<var> vars;
    private DataCollection data;
    public TriggerAction(String name, String concept,valueConstraint constraint, Context _context) {
        super(ActionType.Trigger, name, concept, _context);
        vars=new Vector<var>();

        SubscribeConcept(concept);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String concept = intent.getStringExtra("concept");
        String val = String.valueOf(intent.getStringExtra("value"));
        DataItem item;
        Date dateNow=null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:sszzz");
        String now = sdf.format(new Date());

        if(data!=null)
        {
            try {
                dateNow=sdf.parse(now);
            } catch (ParseException e) {
                Log.e("Trigger action","error parsing date");
            }
            item=new DataItem(concept,val,dateNow);
           //data.insertItem(item);
        }

        

        Intent i = new Intent("trigger2");

        Log.i("trigger from Action", "trigger from action " + actionName);
    }

    public void addDataItem(DataItem item)
    {
        //data.insertItem(item);
    }




    public void addValueConstrainsts(String varName,String concept, var.Operators op,String value)
    {
        boolean found=false;
        var v=getVar(varName);
        if(v!=null)
        {
            v.addValueConstraint(concept,op,value);
        }


    }


    @Override
    public void doAction() {

    }
}
