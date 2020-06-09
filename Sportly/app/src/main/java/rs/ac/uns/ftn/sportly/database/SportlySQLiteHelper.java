package rs.ac.uns.ftn.sportly.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SportlySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sportly.db";
    private static final int DATABASE_VERSION = 8;

    public SportlySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataBaseTables.FRIENDS_CREATE);
        db.execSQL(DataBaseTables.SPORTSFIELDS_CREATE);
        db.execSQL(DataBaseTables.EVENTS_CREATE);
    }

   @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseTables.TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseTables.TABLE_SPORTSFIELDS);
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseTables.TABLE_EVENTS);
        onCreate(db);
    }
}
