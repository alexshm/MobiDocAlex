package example.com.mobidoc;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.Projection;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import projections.projection;

/**
 * Created by Moshe on 2/6/2015.
 */
public class projectionsCollection {
    private ExecutorService threadPool;
    private Hashtable<String,projection>projectionCollection;

    private static projectionsCollection instance;

    public static projectionsCollection getInstance() {

        if (instance == null)
            return new projectionsCollection();
        return instance;
    }


    private  projectionsCollection (){
        projectionCollection=new Hashtable<String,projection>();
        threadPool=Executors.newFixedThreadPool(5);
        instance=this;
    }

    public  void startProjection(String projectionID)
    {

            projection p = projectionCollection.get(projectionID);
            Log.i("projectionsCollection","startProjection-starting projection : "+p.getProjectionId());
            threadPool.execute(p);


    }
    public synchronized void  addProjection(projection  p)
    {
        System.out.println("proj id : "+p.getProjectionId());
      projectionCollection.put(p.getProjectionId(),p);


    }

    public synchronized int getCollectionSize()
    {
        return projectionCollection.size();
    }
    public synchronized Enumeration getAllProjections()
    {
        return  projectionCollection.elements();
    }

    public synchronized projection getprojection(String projectionID)

    {

        return projectionCollection.get(projectionID);
    }


}
