package projections.mobiDocProjections;


import android.content.Context;
import android.content.ContextWrapper;


import projections.Actions.compositeAction;
import projections.CyclicProjectionAbstract;
import projections.Actions.MeasurementAction;
import projections.Utils;
import projections.projection;

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
        switch (keyID) {
            case "19964":
                cyc = new CyclicProjectionAbstract(keyID, cont, "") {

                    @Override
                    public void doAction() {
                        System.out.println("this is a cyclic proj Ketonuria");
                    }

                    @Override
                    public void makeTestCyclic() {
                        compositeAction ac=new compositeAction(cont, Utils.ExecuteMode.Parallel);

                        MeasurementAction m1 = new MeasurementAction("mesure Ketonuria", "5021", cont);
                        ac.addAction(m1);


                       // setAction(m1);

                        //setFrequency(Minute,2);
                        //setReaminder(Second,30);
                    }
                };
                break;
            case "20093":
                cyc = new CyclicProjectionAbstract(keyID, cont, "") {
                    @Override
                    public void doAction() {
                        System.out.println("this is a cyclic proj Routine Daily BG - launch");
                    }

                    @Override
                    public void makeTestCyclic() {
                        MeasurementAction m1 = new MeasurementAction("BG Lunch", "4987", cont);
                        //setAction(m1);

                        //setFrequency(Minute,1);
                        // setReaminder(Second,40);
                    }


                };
                break;
            case "20092":
                cyc = new CyclicProjectionAbstract(keyID, cont, "") {
                    @Override
                    public void doAction() {
                        System.out.println("this is a cyclic proj Routine Daily BG-Breakfast");
                    }

                    @Override
                    public void makeTestCyclic() {
                        MeasurementAction m1 = new MeasurementAction("BG Breakfast", "4986", cont);
                       // this.setAction(m1);

                        //setFrequency(Second,30);
                        //setReaminder(Second,40);
                    }
                };
                break;

            case "20091":
                cyc = new CyclicProjectionAbstract(keyID, cont, "") {
                    @Override
                    public void doAction() {
                        System.out.println("this is a call back test");
                    }

                    @Override
                    public void makeTestCyclic() {
                        MeasurementAction m1 = new MeasurementAction("BG Fasting", "4985", cont);
                       // this.setAction(m1);

                        //setFrequency(Second,50);
                        //setReaminder(Second,5);
                    }
                };
                break;
            case "20026":
                cyc = new CyclicProjectionAbstract(keyID, cont, "") {
                    @Override
                    public void makeTestCyclic() {
                        MeasurementAction m1 = new MeasurementAction("BG Fasting", "4985", cont);
                        //this.setAction(m1);
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
