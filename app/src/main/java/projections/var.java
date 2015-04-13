package projections;

import android.util.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.aggregate.Aggregator;
import ch.lambdaj.function.argument.Argument;
import projections.monitoringObjects.DataCollection;
import projections.monitoringObjects.valueConstraint;


import static ch.lambdaj.Lambda.*;
import java.lang.Iterable;

/*


 */
public class var<T> {

    private String name;
    private VarType type;
    private T  val;
    private Operators operator;

    private int count;
    private Vector<valueConstraint> valConstraints;
    private String concept;

    private OperationBetweenConstraint betweenConstraint;
    public enum VarType {
        Int,String,Char,Double,Null
    }

    public enum Operators
    {
        Equal,GreaterThen,LessThen,GreatEqual,LessEqual
    }


    public enum OperationBetweenConstraint
    {
        Or,And
    }
    public var(String _name,String varConcept,VarType _type)

    {
        name = _name;
        type = _type;
        concept=varConcept;
        valConstraints=new Vector<valueConstraint>();
        betweenConstraint=OperationBetweenConstraint.Or;
    }

    public String getConcept()
    {
        return concept;
    }
    public void addValueConstraint(String concept, Operators op, String val)
    {
        valueConstraint valc=new valueConstraint(concept,op,val);
        valConstraints.add(valc);

    }


    //By default the Operation between Constraint is OR
    public void setOpBetweenValueConstraints(OperationBetweenConstraint op)
    {
        betweenConstraint=op;
    }



    public Operators getOperator()
    {
        return operator;
    }

    public String getName()
    {
        return name;
    }


    public boolean isSatisfyVar(String val)
    {

        if(betweenConstraint.equals(OperationBetweenConstraint.And))
        {
            boolean ans=true;
            for(valueConstraint constraint:valConstraints) {
                ans = ans && constraint.isSatisfyConstraint(val);
            }
            return  ans;
        }
        else {
            boolean ans = false;
            for (valueConstraint constraint : valConstraints) {

                ans = ans || constraint.isSatisfyConstraint(val);
            }
            return ans;
        }

    }


}
