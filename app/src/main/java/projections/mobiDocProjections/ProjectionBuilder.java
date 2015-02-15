package projections.mobiDocProjections;


import android.content.Context;
import android.content.ContextWrapper;

import projections.CyclicProjection;
import projections.CyclicProjectionAbstract;
import projections.MeasurementAction;
import projections.projection;

import static projections.projection.ProjectionTimeUnit.*;

public class ProjectionBuilder {

    private Context cont;

    public ProjectionBuilder(Context c)
    {
       // String _jsontest=
        cont=new ContextWrapper(c);
    }

    public projection SimulateBuild_Projection(String keyID)

    {
        projection cyc=null;
        switch (keyID)
        {
            case "19964":
                cyc=new CyclicProjectionAbstract(keyID,cont) {
                    @Override
                    public void doAction() {
                        System.out.println("this is a cyclic proj Ketonuria");
                    }
                    @Override
                    public void makeTestCyclic()
                    {
                        MeasurementAction m1=new MeasurementAction("mesure Ketonuria","5021",cont);
                        this.setAction(m1);

                        setFrequency(Second,30);
                    }


                };
                break;
            case "20093":
                cyc=new CyclicProjectionAbstract(keyID,cont) {
                    @Override
                    public void doAction() {
                        System.out.println("this is a cyclic proj Routine Daily BG");
                    }
                    @Override
                    public void makeTestCyclic()
                    {
                        MeasurementAction m1=new MeasurementAction("Routine Daily BG lunch measurement","4987",cont);
                        this.setAction(m1);

                        setFrequency(Second,50);
                    }


                };
                break;




        }

        return  cyc;
    }

    public projection BuildProjection()
    {
        return  null;

    }
}
