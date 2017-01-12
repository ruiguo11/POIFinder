package app.android.example.com.poifinder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ruiguo on 8/11/16.
 */

public class PlaceDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    private static String TAG = "PlaceDbHelper";

    static final String DATABASE_NAME = "places.db";

    public PlaceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_Place_TABLE = "CREATE TABLE " + PlaceContract.PlaceEntry.TABLE_NAME + " (" +
                PlaceContract.PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PlaceContract.PlaceEntry.COLUMN_PLACE_ID + " TEXT NOT NULL, " +
                PlaceContract.PlaceEntry.COLUMN_PLACE_NAME + " TEXT NOT NULL, " +
                PlaceContract.PlaceEntry.COLUMN_PLACE_PHONE + " TEXT, " +
                PlaceContract.PlaceEntry.COLUMN_PLACE_ADDRESS + " TEXT, " +
                PlaceContract.PlaceEntry.COLUMN_PLACE_RATING + " REAL, " +
                PlaceContract.PlaceEntry.COLUMN_PLACE_PHOTO + " TEXT, " +
                PlaceContract.PlaceEntry.COLUMN_PLACE_HOURS + " TEXT, " +
                PlaceContract.PlaceEntry.COLUMN_PLACE_TYPE + " TEXT, " +
                PlaceContract.PlaceEntry.COLUMN_PLACE_REVIEW + " TEXT )";
        Log.d(TAG, SQL_CREATE_Place_TABLE);
        db.execSQL(SQL_CREATE_Place_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + PlaceContract.PlaceEntry.TABLE_NAME);
        onCreate(db);
    }
}