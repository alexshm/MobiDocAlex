package projections;


import android.content.Context;

public  class CyclicProjection extends CyclicProjectionAbstract {

    public CyclicProjection(String projectionName, Context c) {
        super(projectionName, c);

    }

    public void makeTestCyclic()
    {
        MeasurementAction m1=new MeasurementAction("mesure BP","78",this.context);
        super.setAction(m1);
    }
    @Override
    public void doAction() {
        MeasurementAction m1=new MeasurementAction("mesure BP","78",this.context);
    }
}
