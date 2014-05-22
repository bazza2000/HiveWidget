package org.impact.android.hivewidget.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.impact.android.hivewidget.HiveWidgetProvider;
import org.impact.android.hivewidget.R;
import org.impact.android.hivewidget.json.HiveJSONHelper;
import org.impact.android.hivewidget.prefs.HiveWidgetConfigure;
import org.json.JSONException;
import org.json.JSONObject;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

public class HiveHTTPHelper extends AsyncTask<Void, Void, Integer> {
	private final String TAG = "HiveHTTPHelper";
	private static final String LOGGED_IN_URL = "https://www.hivehome.com/myhive/dashboard";
	private static final String WEATHER_URL = "https://www.hivehome.com/myhive/weather" ;
	private static final String CH_TARGET = "https://www.hivehome.com/myhive/heating/target" ;
	private HttpResponse response;
	private InputStream is;
	private BufferedReader in;
	private StringBuilder json_reply;
	private String username;
	private String password;
	private String url = "https://www.hivehome.com/login";
	private StringEntity entity;
	private HttpEntity httpentity;
	private RemoteViews rv;
	private int WidgetID;
	private AppWidgetManager WidgetManager;
	private Context context;
	private String weather;
	private int status_code ;
	private HttpClient httpclient ;
	private String weather_data ;
	private String ch_target_data ;

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();


	}

	public HiveHTTPHelper(RemoteViews rv, int appWidgetID,
			AppWidgetManager appWidgetManager, Context context) {
		Log.d(TAG, "HiveHTTPHelper->Constructor running");
		this.rv = rv;
		this.WidgetID = appWidgetID;
		this.WidgetManager = appWidgetManager;
		this.context = context;
	}

	private JSONObject getJSONLoginObject() {
		JSONObject jsonObj = new JSONObject();

		username = HiveWidgetConfigure.loadPref(context, "email");
		password = HiveWidgetConfigure.loadPref(context, "password");

		try {
			jsonObj.put("password", password);
			jsonObj.put("username", username);
		} catch (JSONException e) {
			Log.e(TAG, "JSONException: " + e);
		}
		return jsonObj ;
	}

	@Override
	protected Integer doInBackground(Void... params) {

		rv.setTextViewText(R.id.tv_inside_temp, "Updating Status");
		WidgetManager.updateAppWidget(WidgetID, rv);

		String PROXY_HOST = "10.0.2.2";
		int PROXY_PORT = 3128;

		httpclient = new DefaultHttpClient();

		// Comment out the following 3 lines to stop using proxy and go direct
		if (Build.FINGERPRINT.startsWith("generic")) {
			HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		}

		// Create a local instance of cookie store
		CookieStore cookieStore = new BasicCookieStore();

		// Create local HTTP context
		HttpContext localContext = new BasicHttpContext();

		// Bind custom cookie store to the local context
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		// Create the POST object and add the url
		HttpPost httpPost = new HttpPost(url);

		// Add the json formatted username and password to the entity
		try {
			entity = new StringEntity(getJSONLoginObject().toString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		// Set content type to json
		entity.setContentType("application/json");

		// Add entity to POST object
		httpPost.setEntity(entity);

		// Execute the request
		try {
			response = httpclient.execute(httpPost, localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		if (response != null) {
			// Get response entity
			httpentity = response.getEntity();

			// Extract redirect URL
			HttpUriRequest currentReq = (HttpUriRequest) localContext
					.getAttribute(ExecutionContext.HTTP_REQUEST);

			HttpHost currentHost = (HttpHost) localContext
					.getAttribute(ExecutionContext.HTTP_TARGET_HOST);

			String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq
					.getURI().toString() : (currentHost.toURI() + currentReq
							.getURI());

					// Check URL to confirm if we logged in okay.
					if (!currentUrl.equals(LOGGED_IN_URL)) {
						Log.d(TAG, "LOGIN FAILED");
						rv.setTextViewText(R.id.tv_inside_temp, "Login Failed..");
						// Set auth failed http code (401) as returned code is 200 for some reason.
						status_code = 401 ;
					} else {
						Log.d(TAG, "LOGIN OK");
						rv.setTextViewText(R.id.tv_inside_temp, "Login Successful");

						// Extract retrieved cookie information
						// This is not really required but left here for debugging purposes.
						List<Cookie> cookies = cookieStore.getCookies();
						for (int i = 0; i < cookies.size(); i++) {
							Log.d(TAG, "Local cookie: " + cookies.get(i));
						}

						try {
							is = httpentity.getContent();
							in = new BufferedReader(new InputStreamReader(is));

							json_reply = new StringBuilder();
							String inputLine;

							while ((inputLine = in.readLine()) != null)
								json_reply.append(inputLine);

						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						// Get CH Target info
						ch_target_data = getJsonfromURL(CH_TARGET, httpclient, localContext) ;
						// Get Weather info
						weather_data = getJsonfromURL(WEATHER_URL, httpclient, localContext) ;
					}

		} else {
			Log.d(TAG, "httpclient.execute returned null");
			rv.setTextViewText(R.id.tv_inside_temp, "Response Error..");
		}
		// Redraw widget
		WidgetManager.updateAppWidget(WidgetID, rv);
		return status_code;
	}

	@Override
	protected void onPostExecute(Integer status_code) {
		super.onPostExecute(status_code);

		// Log.d(TAG, "Response" + json_reply.toString());
		Log.d(TAG, "Pre: HiveJSONHelper.getInsideTemp");

		if (status_code == 200) {
			Log.d(TAG, "Status:" + status_code + " Returned");

			double inside_temp;
			double outside_temp;
			double target;
			String city;
			try {
				inside_temp = HiveJSONHelper.getInsideTemp(weather_data);
				outside_temp = HiveJSONHelper.getOutsideTemp(weather_data);
				weather = HiveJSONHelper.getWeather(weather_data);
				city = HiveJSONHelper.getCity(weather_data);
				target = HiveJSONHelper.getCHTarget(ch_target_data);

				Log.d(TAG, "Inside:" + inside_temp);
				Log.d(TAG, "Outside:" + outside_temp);
				Log.d(TAG, "Weather:" + weather);
				Log.d(TAG, "City:" + city);
				Log.d(TAG, "Target:" + target);

				Log.d(TAG, "In: " + inside_temp + "(" + target + ")" + "\nOut:" + outside_temp + "\n"
						+ city);
				char degree = '\u00B0';
				rv.setTextViewText(R.id.tv_inside_temp, "In: " + inside_temp + degree + " (" + target + degree + ")" );
				rv.setTextViewText(R.id.tv_city, city + " (" + outside_temp + degree + ")");
				WidgetManager.updateAppWidget(WidgetID, rv);

				if ((Double.compare(inside_temp, target)) < 0) {
					rv.setTextColor(R.id.tv_inside_temp, Color.RED);
					WidgetManager.updateAppWidget(WidgetID, rv);
				} else {
					rv.setTextColor(R.id.tv_inside_temp, Color.BLUE);
					WidgetManager.updateAppWidget(WidgetID, rv);
				}

				// Get update time
				Time now = new Time();
				now.setToNow();
				String update_time = String.format(Locale.UK, "%02d:%02d:%02d",
						now.hour, now.minute, now.second);

				Log.d(TAG, "Last update @ " + update_time);

				// Update widget
				rv.setTextViewText(R.id.tv_update_time, "Last update @ "
						+ update_time);
				rv.setImageViewBitmap(R.id.iv_weather, getWeatherIconBitmap());
				WidgetManager.updateAppWidget(WidgetID, rv);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			Log.d(TAG, "Status:" + status_code + " Returned");
			if (status_code == 401) {
				rv.setTextViewText(R.id.tv_inside_temp, "Login Failed");
				WidgetManager.updateAppWidget(WidgetID, rv);
			} else {
				rv.setTextViewText(R.id.tv_inside_temp, "Error Status Code=" + status_code);
				WidgetManager.updateAppWidget(WidgetID, rv);
			}
		}

		Log.d(TAG, "RC(" + status_code + ") complete");

		
	}

	private Bitmap getWeatherIconBitmap() {
		Bitmap bm = Bitmap.createBitmap(150, 150,
				Bitmap.Config.ARGB_4444);
		Canvas cs = new Canvas(bm);
		Paint paint = new Paint();
		Typeface typeFace = Typeface.createFromAsset(
				context.getAssets(), "fonts/hivecons.ttf");
		paint.setAntiAlias(true);
		paint.setSubpixelText(true);
		paint.setTypeface(typeFace);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTextSize(150);
		// Display generic * icon when we first start.
		// char weather_char = '\uF054' ; // Sad Face
		// char weather_char = '\uF047' ; // Thunder Storms
		char weather_char = HiveJSONHelper
				.getUniCodeWeatherChar(weather);
		cs.drawText("" + weather_char, 10, 120, paint);
		// myCanvas.drawColor(Color.GREEN);
		return bm ;
	}

	private String getJsonfromURL(String url, HttpClient httpclient, HttpContext localContext) {
		// GET temp data, send cookie for auth
		HttpGet httpGet = new HttpGet(url) ;

		// Next line required to return JSON format response.
		httpGet.setHeader("X-Requested-With", "XMLHttpRequest");

		Log.d(TAG, "executing request " + httpGet.getURI());

		try {
			response = httpclient.execute(httpGet, localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d(TAG, "request executed " + response.getStatusLine());

		status_code = response.getStatusLine().getStatusCode();

		if (status_code == 200) {
			rv.setTextViewText(R.id.tv_inside_temp, "Parsing Data");
		} else {
			rv.setTextViewText(R.id.tv_inside_temp, "Query Failed..(" + status_code
					+ ")");
		}
		WidgetManager.updateAppWidget(WidgetID, rv);

		httpentity = response.getEntity();

		try {
			is = httpentity.getContent();
			in = new BufferedReader(new InputStreamReader(is));

			json_reply = new StringBuilder();
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				json_reply.append(inputLine);
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null ;
		} catch (IOException e) {
			e.printStackTrace();
			return null ;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return json_reply.toString();
	}
}
