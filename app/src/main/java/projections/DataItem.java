package projections;


import java.util.Date;

public class DataItem {

    private String concept;
    private String value;
    private  Date date;



    public DataItem(String _concept,String val,Date time)
    {
        concept=_concept;
        value=val;
        date=time;
    }

    public String getConcept()
    {
        return concept;
    }
    public String getVal()
    {
        return value;
    }

    public Date getItemDate()
    {
        return date;
    }

    public  DataItem(DataItem other)
    {
        this.concept=other.getConcept();
        this.date=other.getItemDate();
        this.value=other.getVal();
    }


}
