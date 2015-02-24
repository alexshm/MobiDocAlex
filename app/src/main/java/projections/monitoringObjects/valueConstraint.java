package projections.monitoringObjects;


import projections.var;
import android.text.TextUtils;
public class valueConstraint {

    private String TriggerName;
    private var.Operators operator;

    private ConditionActions action;
    private String constraintVal;


    public valueConstraint(String triggerName, var.Operators op, String val)
    {
        TriggerName=triggerName;

        constraintVal=val;

        operator=op;

    }

    public boolean isSatisfyConstraint(String val)
    {
        boolean isConstraintInt=android.text.TextUtils.isDigitsOnly(constraintVal);
        boolean ans=false;
        if(isConstraintInt) {

            int valToCheck = Integer.parseInt(val);
            int targerVal = Integer.parseInt(constraintVal);
            switch (operator) {
                case GreaterThen:
                    ans = valToCheck > targerVal;
                    break;
                case GreatEqual:
                    ans = valToCheck >= targerVal;
                    break;
                case Equal:
                    ans = valToCheck == targerVal;
                    break;
                case LessEqual:
                    ans = valToCheck <= targerVal;
                    break;
                case LessThen:
                    ans = valToCheck < targerVal;
                    break;
            }
        }
        else
        {
            //the constraint is string value
            ans=constraintVal.equals(val);
        }

        return ans;

    }

    public enum ConditionActions{
        Sum,Count,Avg,Eq
    }

    public enum MonitorOperators
    {
        Equal,GreaterThen,LessThen,GreatEqual,LessEqual
    }
}
