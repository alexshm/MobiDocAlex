package projections;


import android.content.Context;

public class MonitorAction extends  Action {


    public MonitorAction( String name, String concept, Context _context) {
        super(ActionType.Trigger, name, concept, _context);

        SubscribeConcept(concept);
    }


    public void setConcept( String conceptId)
    {
       actionConcept=conceptId;
       SubscribeConcept(conceptId);
    }
    @Override
    public void doAction() {

    }
}
