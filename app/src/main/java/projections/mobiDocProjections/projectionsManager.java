package projections.mobiDocProjections;

import android.content.Context;
import android.widget.Toast;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


import projections.projection;

/**
 * Created by Moshe on 2/6/2015.
 */
public class projectionsManager {


    private ProjectionBuilder pb;
 private Hashtable<String,projection>projectionCollection;


    public projectionsManager(Context context)
    {
        projectionCollection=new Hashtable<String,projection>();



        //insert all the Projecction to the dictionary
        // ***For now its just Simulate the insertion***
        pb=new ProjectionBuilder(context);
        //TODO: change to convert from json to the projection




    }

    public void  addProjection(String name, projection.ProjectionType type, final String keyName, final Context c)
    {



    }
    public Enumeration getAllProjections()
    {
        return  projectionCollection.elements();
    }

    public projection getprojection(String concept)

    {

        return projectionCollection.get(concept);
    }


}
