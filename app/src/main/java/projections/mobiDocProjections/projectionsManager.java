package projections.mobiDocProjections;

import android.content.Context;
import android.widget.Toast;

import java.util.Enumeration;
import java.util.Vector;

import projections.CyclicProjection;
import projections.projection;

/**
 * Created by Moshe on 2/6/2015.
 */
public class projectionsManager {

    private Vector<projection>projectionCollection;
    private ProjectionBuilder pb;



    public projectionsManager(Context context)
    {
        projectionCollection=new Vector<projection>();

        //insert all the Projecction to the dictionary
        // ***For now its just Simulate the insertion***
        pb=new ProjectionBuilder(context);
        //TODO: change to convert from json to the projection

        //**** SIMULATE INSERTION ***
        //==============================

        SimulateInsertionProjecctions(context);

    }

    public void  addProjection(String name, projection.ProjectionType type, final String keyName, final Context c)
    {



    }
    public Enumeration getAllProjections()
    {
        return  projectionCollection.elements();
    }

    public projection getprojection()
    {
        return  projectionCollection.get(0);
    }

    private void SimulateInsertionProjecctions( final Context c)
    {
        //projection # 19964
        //=========================
        projection proj=pb.SimulateBuild_Projection("19964");
           projectionCollection.add(proj);

        //projection # 20093
        //=========================
        projection proj1=pb.SimulateBuild_Projection("20093");
        projectionCollection.add(proj1);

    }
}
