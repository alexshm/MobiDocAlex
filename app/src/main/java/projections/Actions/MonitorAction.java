package projections.Actions;


import android.content.Context;

public class MonitorAction extends  Action {


    public MonitorAction( String name, Context _context) {
        super(ActionType.Trigger, name, "", _context);
    }

    public void setOnSuccess(compositeAction a)
    {
        //TODO:  implement
    }
    public void setOnFail(compositeAction a)
    {
        //TODO: implement
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
