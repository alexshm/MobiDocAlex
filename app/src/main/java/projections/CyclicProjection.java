package projections;


import android.content.Context;

public  class CyclicProjection extends CyclicProjectionAbstract {

    private int a=2;
    public CyclicProjection(String projectionName, Context c) {
        super(projectionName, c);

    }

    public void makeTestCyclic()
    {
        System.out.println("value of a : "+a);
    }


    @Override
    public void doAction() {
        System.out.println("this is a cyclic proj");
    }
}
