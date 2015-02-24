package projections;

import android.content.Context;

public class MeasurementAction extends  Action{

    public MeasurementAction(String measureName,  String conceptId,Context c)
    {
        super(ActionType.Measurement, measureName, conceptId, c);
        SubscribeConcept(conceptId);
        defineVar("Ketanuria anbormal", var.VarType.Int);

        addValueConstraint("Ketanuria anbormal", "5021", var.Operators.GreaterThen, "85");
        setAggregationConstraint("Ketanuria anbormal", var.AggregationAction.Count, var.Operators.GreaterThen,2);

    }



    @Override
     public void doAction() {

    }
}
