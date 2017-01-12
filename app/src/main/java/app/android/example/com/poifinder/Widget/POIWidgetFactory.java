package app.android.example.com.poifinder.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import app.android.example.com.poifinder.R;
import app.android.example.com.poifinder.data.PlaceContract;

/**
 * Created by ruiguo on 8/11/16.
 */
public class POIWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor cursor;
    private int appWidgetId;
    private Context mContext;
    private static final String TAG= "StockWidgetFactory";


    public POIWidgetFactory(Context context, Intent intent){
        Log.d(TAG, "constructor");
        cursor = null;
        mContext =context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "OnCreate");
        cursor = mContext.getContentResolver().query(
                PlaceContract.PlaceEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );


    }

    @Override
    public void onDataSetChanged() {

        Log.d(TAG, "OnDataSetChanged");
        if(cursor!=null){
            cursor.close();
        }
       cursor = mContext.getContentResolver().query(
                PlaceContract.PlaceEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestory");
        if(cursor!=null){
            cursor.close();
            cursor=null;
        }


    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount"+cursor.getCount());

        return cursor==null? 0 :cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(position)) {
            return null;
        }
        Log.d(TAG, "getViewAt "+position);

        if (cursor.moveToPosition(position)) {
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.poi_item_widget);

            //remoteViews.setImageViewUri(R.id.widget_item_photo, Uri.parse(cursor.getString(6)));
            remoteViews.setTextViewText(R.id.widget_poi_name, cursor.getString(2));

            //Log.d("photo url ", Uri.parse(cursor.getString(6)).toString());
            Log.d("name", cursor.getString(2));
            Intent fillInIntent = new Intent();
            fillInIntent.putExtra("id", cursor.getString(1));
            remoteViews.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

            /*
            RemoteViews remoteView = new RemoteViews( mContext.getPackageName(), R.layout.stock_item_widget);
            remoteView.setTextViewText(R.id.widget_stock_symbol, cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)));
            remoteView.setTextViewText(R.id.widget_bid_price, cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)));

            String changeString = cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE));
            remoteView.setTextViewText(R.id.widget_change, changeString);
            if (Float.parseFloat(changeString) >= 0) {
                remoteView.setTextColor(R.id.widget_change, Color.GREEN);
            } else {
                remoteView.setTextColor(R.id.widget_change, Color.RED);
            }
            Log.d(TAG, cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)));





            fillInIntent.putExtra(QuoteColumns.SYMBOL, cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)));
            Log.d(TAG, "fillIntent"+fillInIntent.getStringExtra(QuoteColumns.SYMBOL));
            remoteView.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
            return remoteView;

        */
            return remoteViews;
        }
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {

        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
