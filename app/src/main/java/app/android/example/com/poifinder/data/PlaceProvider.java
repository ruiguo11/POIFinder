package app.android.example.com.poifinder.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;


public class PlaceProvider extends ContentProvider{
    private static final String TAG = "ContentProvider";
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private PlaceDbHelper placeDbHelper;
    static final int PLACE = 1;

    static UriMatcher buildUriMatcher(){
        Log.d(TAG, "buildUriMateher()");
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PlaceContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, PlaceContract.PATH_PLACE, PLACE);

        return matcher;
    }
    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate()");
        placeDbHelper = new PlaceDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query()");
        Cursor retCursor;

        switch (uriMatcher.match(uri)) {
            case PLACE: {
                retCursor = placeDbHelper.getReadableDatabase().query(
                        PlaceContract.PlaceEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        Log.d(TAG, "match="+match);
        switch (match){
            case PLACE:
                return PlaceContract.PlaceEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = placeDbHelper.getWritableDatabase();
        Uri  finalUri;
        Log.d(TAG, "insert"+values.toString());
        switch (uriMatcher.match(uri)){
            case PLACE:{
                long _id = db.insert(PlaceContract.PlaceEntry.TABLE_NAME, null, values);
                Log.d(TAG, "_id"+_id);
                if(_id>0) {
                    finalUri = PlaceContract.PlaceEntry.buildPlaceUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return finalUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = placeDbHelper.getWritableDatabase();

        int rowsDeleted;

        switch (uriMatcher.match(uri)){
            case PLACE:
                rowsDeleted = db.delete(PlaceContract.PlaceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri"+uri);
        }

        if (rowsDeleted !=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }



    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = placeDbHelper.getWritableDatabase();
        int rowsUpdated;
        switch (uriMatcher.match(uri)){
            case PLACE:
                rowsUpdated = db.update(PlaceContract.PlaceEntry.TABLE_NAME,  values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
