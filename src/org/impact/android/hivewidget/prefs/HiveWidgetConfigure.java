package org.impact.android.hivewidget.prefs;

import org.impact.android.hivewidget.HiveWidgetProvider;
import org.impact.android.hivewidget.HiveWidgetService;
import org.impact.android.hivewidget.R;
import org.impact.android.hivewidget.http.HiveHTTPHelper;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import android.widget.RemoteViews;

/**
 * The configuration screen for the ExampleAppWidgetProvider widget sample.
 */
public class HiveWidgetConfigure extends Activity {
	static final String TAG = "HiveWidgetConfigure";

	private static final String PREFS_NAME = "org.impact.android.hivewidget.prefs.HiveWidgetConfigure";

	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	EditText et_email, et_password;
	String email, password;

	public HiveWidgetConfigure() {
		super();
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// Set the result to CANCELLED. This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		setResult(RESULT_CANCELED);

		// Set the view layout resource to use.
		if (Build.FINGERPRINT.startsWith("generic")) {
			setContentView(R.layout.prefs_layout_sdk);
		} else {
			setContentView(R.layout.prefs_layout);
		}

		// Find the EditText

		et_email = (EditText) findViewById(R.id.et_email);
		et_password = (EditText) findViewById(R.id.et_password);

		final Context context = HiveWidgetConfigure.this;

		et_email.setText(loadPref(context, "email"));
		et_password.setText(loadPref(context, "password"));

		// Bind the action for the save button.
		findViewById(R.id.b_save).setOnClickListener(mOnClickListener);

		// Find the widget id from the intent. 
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {

			mAppWidgetId = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			Log.d(TAG,"Extras != null getting EXTRA_APPWIDGET_ID = " + mAppWidgetId);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			Log.d(TAG,"INVALID_APPWIDGET_ID Bailing...");
			finish();
		}
	}

	View.OnClickListener mOnClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			final Context context = HiveWidgetConfigure.this;

			// When the save button is clicked, save the string in our prefs

			password = et_password.getText().toString();
			email = et_email.getText().toString();

			savePref(context, "password", password);
			savePref(context, "email", email);

			RemoteViews remoteViews ;

			if (Build.FINGERPRINT.startsWith("generic")) {
				remoteViews = new RemoteViews(context.getPackageName(),
						R.layout.widget_layout_sdk);

			} else {
				remoteViews = new RemoteViews(context.getPackageName(),
						R.layout.widget_layout);
			}

			// Build/Update widget
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

			// Make sure we pass back the original appWidgetId
			Intent resultValue = new Intent();
			Log.d(TAG,"in OnClick with mAppWidgetId=" + mAppWidgetId) ;
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			setResult(RESULT_OK, resultValue);

			Log.d(TAG,"Initiating an update after configuration has been completed") ;
			
			// Build the intent to call the service
			Intent intent = new Intent(context.getApplicationContext(),
					HiveWidgetService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {mAppWidgetId});

			// Update the widgets via the service
			context.startService(intent);
//			new HiveHTTPHelper(remoteViews, mAppWidgetId, appWidgetManager,
//					getApplicationContext()).execute();

			// Destroy activity
			finish();
		}
	};

	// Write the password to the SharedPreferences object for this widget
	static void savePref(Context context, String key, String text) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(key, text);
		prefs.commit();
	}


	public static String loadPref(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String value = prefs.getString(key, null);
		if (value != null) {
			return value;
		} else {
			return "empty";
		}
	}
}