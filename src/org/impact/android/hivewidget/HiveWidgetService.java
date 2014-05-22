package org.impact.android.hivewidget;

import java.util.Random;

import org.impact.android.hivewidget.prefs.HiveWidgetConfigure;
import org.impact.android.hivewidget.http.HiveHTTPHelper;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class HiveWidgetService extends Service {
	private static final String TAG = "HiveWidgetService";
	public static String ACTION_WIDGET_SETTINGS = "HiveWidgetSettings";
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "onStart Called");
		// create some random data

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
				.getApplicationContext());

		int[] allWidgetIds = intent
				.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		ComponentName thisWidget = new ComponentName(getApplicationContext(),
				HiveWidgetProvider.class);
		int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);
		Log.d(TAG, "From Intent" + String.valueOf(allWidgetIds.length));
		Log.d(TAG, "Direct" + String.valueOf(allWidgetIds2.length));

		for (int widgetId : allWidgetIds) {
			Log.d(TAG,"widgetId:" + widgetId) ;
			
			RemoteViews remoteViews = null ;
			
			if (Build.FINGERPRINT.startsWith("generic")) {
				remoteViews = new RemoteViews(this
						.getApplicationContext().getPackageName(),
						R.layout.widget_layout_sdk);
			} else {
				remoteViews = new RemoteViews(this
						.getApplicationContext().getPackageName(),
						R.layout.widget_layout);
			}
			
			ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager
					.getActiveNetworkInfo();
			
			if (HiveWidgetConfigure.loadPref(this.getApplicationContext(), "email") != "empty") {
				if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
					remoteViews.setTextViewText(R.id.tv_inside_temp, "Initiating Login..");
					appWidgetManager.updateAppWidget(widgetId, remoteViews);
					
					new HiveHTTPHelper(remoteViews, widgetId, appWidgetManager,
							this.getApplicationContext()).execute();
				} else {
					remoteViews.setTextViewText(R.id.tv_inside_temp, "No Network..");
					appWidgetManager.updateAppWidget(widgetId, remoteViews);
				}
			} else {
				Log.d(TAG,"onUpdate: Configuration not yet complete") ;
			}

			
			
			
			// Register an onClickListener to initiate an update
			Intent clickIntent = new Intent(this.getApplicationContext(),
					HiveWidgetProvider.class);

			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
					allWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					getApplicationContext(), 0, clickIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.iv_update_click, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
			
			// Register an onClickListener to open configuration screen
			clickIntent = new Intent(this.getApplicationContext(),
					HiveWidgetConfigure.class);

			clickIntent.setAction(ACTION_WIDGET_SETTINGS);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					widgetId);

			pendingIntent = PendingIntent.getActivity(
					getApplicationContext(), 1, clickIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.iv_prefs_click, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);

			
		}
		stopSelf();

		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}