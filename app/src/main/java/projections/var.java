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
    define a variable for monitoring concepts.
    each var is *ONLY* for ONE concept.(meaning var per Concept)

    the type of a  var can be 'int / 'string'

    -** a var also have a collection of valueConstraints.
        a single valueConstraint able to check the value to monitor like : concept #1234 need to be  >= from 90 .
        the var contains  a valueConstraints collection  for multiple Constraints like :
        (i.e : concept 4985 can be >= '90' and <=150  => when  the [ >=90 ] is the value Constraint and
         the collection will be : [ >=90 , <=150 ] )

    ** the 'betweenConstraint' -  sets the operation between Constraint [OR,AND]


    ** valConstraints collection is used for single/multiple values to check.
       (i.e : concept 5021 can be equal '+' or equal '++' or equal '+++'  => when the multiple values to check are :
       ['+','++','+++'])
       in the case of this example :
       (concept 4985 can be >= 90 and <=150  =>  he multiple values to check are  [90,150])

    * Operators - are enum for defines the operator of the value then connected to him .one from : (< / > / = / <= / >=)

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

    /**
     * Constructor
     *
     * @param _name
     * @param varConcept
     * @param _type
     */
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
