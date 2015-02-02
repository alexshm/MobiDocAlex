package projections;

import android.content.Context;

public abstract class CyclicProjection extends projection {


    public CyclicProjection(String projectionName, Context c) {
        super(ProjectionType.Cyclic, projectionName, c);
    }


    public void StartCyclic() {

    }

}
