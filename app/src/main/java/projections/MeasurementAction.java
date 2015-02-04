package projections;

import android.content.Context;

public class MeasurementAction extends  Action{

    public MeasurementAction(String measureName,  String conceptId,Context c)
    {
        super(ActionType.Measurement,measureName,conceptId,c);
        SubscribeConcept(conceptId);

    }



    @Override
     public void doAction() {

    }
}
