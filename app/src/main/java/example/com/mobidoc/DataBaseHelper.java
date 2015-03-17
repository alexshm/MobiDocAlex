package example.com.mobidoc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME="MobiDoc2";
    private static final String TABLE_NAME = "dataitems";

    private static DataBaseHelper instance;

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        "concept" +" TEXT NOT NULL, "+
                        "dateTime TEXT NOT NULL,"+
                        "value TEXT "+
                        "PRIMARY KEY (concept, dateTime));";

        db.execSQL(TABLE_CREATE);
    }
    public static synchronized DataBaseHelper getHelper(Context context) {
        if (instance == null)
            instance = new DataBaseHelper(context);
        return instance;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            // db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
