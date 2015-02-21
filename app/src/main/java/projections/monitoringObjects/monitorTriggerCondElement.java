package projections.monitoringObjects;

/**
 * Created by Moshe on 2/17/2015.
 */
public class monitorTriggerCondElement {

    private String TriggerName;
    private MonitorOperators operator;
    private int targetVal;
    private ConditionActions action;
    private int actualval;


    public monitorTriggerCondElement(String triggerName, ConditionActions _action, MonitorOperators op, int val)
    {
        TriggerName=triggerName;
        targetVal=val;
        actualval=0;
        action=_action;
        operator=op;

    }

    public void addIncomingVal(int val)
    {
        actualval+=val;
    }

    public boolean isCondHappend()
    {
        return actualval>=targetVal;
    }

    public enum ConditionActions{
        Sum,Count,Avg,Eq
    }

    public enum MonitorOperators
    {
        Equal,GreaterThen,LessThen,GreatEqual,LessEqual
    }
}
