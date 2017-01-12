package app.android.example.com.poifinder.Widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import app.android.example.com.poifinder.PlaceDetailsActivity;
import app.android.example.com.poifinder.PlaceListActivity;
import app.android.example.com.poifinder.R;

/**
 * Created by ruiguo on 8/11/16.
 */

public class POIWidgetProvider extends AppWidgetProvider {

    static final String TAG = "POIWidgeProvider";
    //public static final String INTENT_ACTION = "app.android.example.com.poifinder.widget.POIWidgetProvider.INTENT_ACTION";


    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceived");
        super.onReceive(context, intent);
        if (intent.getAction().equals(R.string.INTENT_ACTION)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_poi_collection);

        }

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetIds) {
        Log.d(TAG, "updateAppWidget" + appWidgetIds);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.poi_widget);

        Intent intent = new Intent(context, POIWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds);
        //intent.setAction("UPDATE_WIDGET");
        views.setRemoteAdapter(R.id.widget_poi_collection, intent);


        Intent clickIntentTemplate = new Intent(context, PlaceDetailsActivity.class);


        PendingIntent clickPendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setPendingIntentTemplate(R.id.widget_poi_collection, clickPendingIntent);

        views.setEmptyView(R.id.widget_poi_collection, R.id.widget_empty_view);


        appWidgetManager.updateAppWidget(appWidgetIds, views);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate" + appWidgetIds.length);
        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.poi_widget);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, PlaceListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);

            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

        Log.d(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled

        Log.d(TAG, "onDisabled");
    }
}
