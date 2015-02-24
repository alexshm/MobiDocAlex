package projections;

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


public class var<T> {

    private String name;
    private VarType type;
    private T  val;
    private Operators operator;
    private Operators aggregationOperator;
    private int count;
    private Vector<valueConstraint> valConstraints;
    private String concept;
    private int aggregationTargetVal;
    private AggregationAction aggregationAction;
    public enum VarType {
        Int,String,Char,Double,Null
    }

    public enum Operators
    {
        Equal,GreaterThen,LessThen,GreatEqual,LessEqual
    }

    public enum AggregationAction
    {
        Sum,Avg,Count
    }

    public var(String _name,String varConcept,VarType _type)

    {
        name = _name;
        type = _type;
        concept=varConcept;
        aggregationTargetVal=0;
        aggregationOperator=null;
        aggregationAction=null;
        valConstraints=new Vector<valueConstraint>();
    }

    public void setAggregationAction(AggregationAction action,Operators op,int targetVal)
    {
        aggregationAction=action;
        aggregationOperator=op;
        aggregationTargetVal=targetVal;
    }
    public void addValueConstraint(String concept, Operators op, String val)
    {
        valueConstraint valc=new valueConstraint(concept,op,val);
        valConstraints.add(valc);

    }
    public valueConstraint getValueConstraint()
    {
        return valConstraints.get(0);

    }

    public boolean isSatisfyAggregationonstraint(Iterable data)
    {

        int ans=AggregationFunc(data);
        System.out.println("the func  is : "+  ans);
      return (ans>=aggregationTargetVal);

    }
    public int AggregationFunc(Iterable data)
    {
        switch (aggregationAction)
        {
            case Sum:
                return  sum(data).intValue();

            case Avg:
                return avg(data).intValue();

            case Count:

              return count(data).size();

        }
        return -1;
    }


    public Operators getOperator()
    {
        return operator;
    }

    public String getName()
    {
        return name;
    }

    public boolean isSatisfyConstraint(String val)
    {
       return valConstraints.get(0).isSatisfyConstraint(val);
    }


    public T getVal()
    {

        return  val;
    }


    public void setVal(T newVal)
    {
        val=newVal;
    }




}
