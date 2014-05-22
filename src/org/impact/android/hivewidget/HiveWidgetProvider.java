package org.impact.android.hivewidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class HiveWidgetProvider extends AppWidgetProvider {
	private final String TAG = "HiveHTTPProvider";
	public static String ACTION_WIDGET_SETTINGS = "HiveWidgetSettings";
	
	@Override
	public void onReceive(Context context, Intent intent) {
	    if (intent.getAction().equals(ACTION_WIDGET_SETTINGS)) {
	        Log.d(TAG, "onReceive : ACTION_WIDGET_SETTINGS");
	    } else {
	        super.onReceive(context, intent);
	    }
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(TAG, "onUpdate method called");
		// Get all ids
		ComponentName thisWidget = new ComponentName(context,
				HiveWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		// Build the intent to call the service
		Intent intent = new Intent(context.getApplicationContext(),
				HiveWidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

		// Update the widgets via the service
		context.startService(intent);
	}

}