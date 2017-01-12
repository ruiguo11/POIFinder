package app.android.example.com.poifinder.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ruiguo on 8/11/16.
 */

public class PlaceContract {
    public static final String CONTENT_AUTHORITY = "app.android.example.com.poifinder";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_PLACE = "place";

    public static final class PlaceEntry implements BaseColumns {
        public static final Uri  CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+CONTENT_AUTHORITY+"/"+PATH_PLACE;

        public static final String TABLE_NAME = "place";

        public static final String COLUMN_PLACE_ID = "place_id";

        public static final String COLUMN_PLACE_NAME = "name";

        public static final String COLUMN_PLACE_PHONE= "phone";

        public static final String COLUMN_PLACE_ADDRESS = "adress";

        public static final String COLUMN_PLACE_RATING= "rating";

        public static final String COLUMN_PLACE_PHOTO= "photo";

        public static final String COLUMN_PLACE_HOURS= "opening_hours";

        public static final String COLUMN_PLACE_TYPE= "types";

        public static final String COLUMN_PLACE_REVIEW= "reviews";


        public static Uri buildPlaceUri (long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }
}

