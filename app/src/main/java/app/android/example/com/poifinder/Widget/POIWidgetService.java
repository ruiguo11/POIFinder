package app.android.example.com.poifinder.Widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by ruiguo on 8/11/16.
 */
public class POIWidgetService extends RemoteViewsService {
    private static final String TAG= "POIWidgetService";
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "onGetViewFactory");
        return new POIWidgetFactory(this.getApplicationContext(), intent);
        //return null;
    }
}

