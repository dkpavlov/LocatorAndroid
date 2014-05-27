package fmi.android.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dkpavlov on 1/25/14.
 */
public class SQLHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "locations.db";

    public static final String TABLE_LOCATIONS = "locations";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PICTURE_PATH = "ppath";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "ldate";


    private static final String DATABASE_CREATE = "create table "
            + TABLE_LOCATIONS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_PICTURE_PATH + " text, "
            + COLUMN_LAT + " double, "
            + COLUMN_LNG +" double, "
            + COLUMN_ADDRESS + " text, "
            + COLUMN_DATE + " text);";

    private static final int DATABASE_VERSION = 3;

    private static SQLHelper sInstance = null;

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(sqLiteDatabase);
    }

    public static final synchronized SQLHelper getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new SQLHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public String getTableName(){
        return TABLE_LOCATIONS;
    }

}
