package example.com.mobidoc;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DB  {

    private static final String TABLE_NAME = "dataitems";
    private static final String APP_TABLE_NAME = "AppSettings";
    protected SQLiteDatabase writeableDatabase;
    protected SQLiteDatabase readableDatabase;
    private DataBaseHelper dbHelper;
    private Context mContext;

    private static DB DBInstance;

    public static DB getInstance(Context c) {

        if (DBInstance == null)
            return new DB(c);
        return DBInstance;
    }
    private DB(Context context) {
        this.mContext = context;
        dbHelper = DataBaseHelper.getHelper(mContext);


    }

    private void open() throws SQLException {
        if(dbHelper == null)
            dbHelper = DataBaseHelper.getHelper(mContext);
        writeableDatabase = dbHelper.getWritableDatabase();
        readableDatabase=dbHelper.getReadableDatabase();
    }

    public synchronized String getRegID() {

        open();
        String selectQuery = "SELECT Appkey FROM "+APP_TABLE_NAME;
        String[] params=new String[]{""};
        Cursor c = readableDatabase.rawQuery(selectQuery, null);
        String newval="";
        if(c.moveToFirst()){
            do{
                //assing values
                 newval = c.getString(c.getColumnIndex("Appkey"));
                System.out.println("new val: "+newval);
                //Do something Here with values

            }while(c.moveToNext());
        }

        c.close();
        readableDatabase.close();
        return newval;
    }
    public synchronized String getAppSetting() {

        open();
        String selectQuery = "SELECT * FROM "+APP_TABLE_NAME;
        String[] params=new String[]{""};
        Cursor c = readableDatabase.rawQuery(selectQuery, null);
        String newval="";
        if(c.moveToFirst()){
            do{
                //assing values
                newval += c.getString(c.getColumnIndex("Appkey"));
                newval +="#"+ c.getInt(c.getColumnIndex("version"));

                //Do something Here with values

            }while(c.moveToNext());
        }

        c.close();
        readableDatabase.close();
        return newval;
    }
    public synchronized void  insertRegID(String key,int ver) {
        open();

        ContentValues values = new ContentValues();
        values.put("Appkey", key);
        values.put("version", ver);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;


        newRowId = writeableDatabase.insert(
                APP_TABLE_NAME, "null", values);

        Log.i("inserting to DB", "inserting successfully : key :" + key );
        writeableDatabase.close();

    }

    public synchronized void  insertMesureToDB(String concept,String value,Date time) {
        open();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS.SSS");

        String date = sdf.format(time);
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put("concept", concept);
        values.put("dateTime", date);
        values.put("value", value);
        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        Log.i("inserting to DB", "inserting successfully : concept :" + concept + " value: " + value + "  time: " + time.toString());

        newRowId = writeableDatabase.insert(
                TABLE_NAME, "null", values);
        System.out.println("new eorw is : " + newRowId);
        writeableDatabase.close();

    }
    public synchronized void cleanDB()
    {
        open();

       // long count = writeableDatabase.delete(TABLE_NAME,"1", null);
        writeableDatabase.delete(APP_TABLE_NAME,"1", null);
     //   Log.i("clearDB", "DB is clean .  deleted rows: "+count);
        writeableDatabase.close();
    }
    public synchronized void select()
    {

        open();
        String selectQuery = "SELECT * FROM "+TABLE_NAME;
        String[] params=new String[]{""};
        Cursor c = readableDatabase.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
                //assing values
                String newval = c.getString(c.getColumnIndex("dateTime"));
                System.out.println("new val: "+newval);
                //Do something Here with values

            }while(c.moveToNext());
        }
        Log.i("select to DB", "selecting  successfully ");
        c.close();
        readableDatabase.close();
    }
}





