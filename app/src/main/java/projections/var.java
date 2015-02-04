package projections;

import java.util.Dictionary;
import java.util.Hashtable;

public class var<T> {

    private String name;
    private String type;
    private T  val;
    private Class   varClass;
    private String objtype;
    private   Dictionary<String,VarType> types;


    public var(String _name,String _type)

    {
        name = _name;
        type = _type;
        objtype = type + ".class";
        val=null;
    }


    public enum VarType {
        Int,String,Char,Double,Null

    }

    public var(String _name,String _type,T _val)

    {
        name=_name;
        type=_type;
        objtype=type+".class";
        val=_val;
    }

    public T getVal()
    {

        return  val;
    }


    public void setVal(T newVal)
    {
        val=newVal;
    }

    public void mult(int times)
    {
        if (val instanceof  Integer)
        {
            int currentVal = ((Integer) val).intValue();

            val = (T) new Integer(currentVal * times);

        }
        else if(val instanceof  Double)
        {
            double currentVal = ((Double) val).doubleValue();
            val = (T) new Double(currentVal * times);
        }


    }
    public String getName()
    {
        return name;
    }
    public void IncreaseValBy(double number)
    {
        if (val instanceof  Integer)
        {
            int currentVal = ((Integer) val).intValue();


            val = (T) new Integer(currentVal +(int)number);

        }
        else if(val instanceof  Double)
        {
            double currentVal = ((Double) val).doubleValue();
            val = (T) new Double(currentVal + number);
        }
    }

    public void DecreaseValBy(double number)
    {
        if (val instanceof  Integer)
        {
            int currentVal = ((Integer) val).intValue();


            val = (T) new Integer(currentVal -(int)number);

        }
        else if(val instanceof  Double)
        {
            double currentVal = ((Double) val).doubleValue();
            val = (T) new Double(currentVal - number);
        }
    }
}
