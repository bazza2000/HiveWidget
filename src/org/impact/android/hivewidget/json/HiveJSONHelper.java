package org.impact.android.hivewidget.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HiveJSONHelper {
	private static final String TAG = "HiveJSONHelper";
	
	public enum Weather {
		heavy_snow_showers, thundery_showers, thunderstorms, cloudy_with_sleet, sleet_showers, light_snow_showers, light_snow, cloudy_with_heavy_rain, heavy_rain_showers, light_rain_showers, cloudy_with_heavy_snow, cloudy_with_light_snow, cloudy_with_light_rain, fog, mist, black_low_cloud, white_cloud, sunny_intervals, sunny, clear_sky
	}
	
	public static Date getLatestTime(String content) {
		// content =
		// "{\"data\":[{\"date\":\"2013-11-11T00:00:00.000Z\",\"temperature\":21.2},{\"date\":\"2013-11-11T00:30:00.000Z\",\"temperature\":20.99},{\"date\":\"2013-11-11T01:00:00.000Z\",\"temperature\":20.77},{\"date\":\"2013-11-11T01:30:00.000Z\",\"temperature\":20.56},{\"date\":\"2013-11-11T02:00:00.000Z\",\"temperature\":20.37},{\"date\":\"2013-11-11T02:30:00.000Z\",\"temperature\":20.15},{\"date\":\"2013-11-11T03:00:00.000Z\",\"temperature\":20.01},{\"date\":\"2013-11-11T03:30:00.000Z\",\"temperature\":19.81},{\"date\":\"2013-11-11T04:00:00.000Z\",\"temperature\":19.65},{\"date\":\"2013-11-11T04:30:00.000Z\",\"temperature\":19.5},{\"date\":\"2013-11-11T05:00:00.000Z\",\"temperature\":19.43},{\"date\":\"2013-11-11T05:30:00.000Z\",\"temperature\":19.82},{\"date\":\"2013-11-11T06:00:00.000Z\",\"temperature\":20.24},{\"date\":\"2013-11-11T06:30:00.000Z\",\"temperature\":20.47},{\"date\":\"2013-11-11T07:00:00.000Z\",\"temperature\":20.69},{\"date\":\"2013-11-11T07:30:00.000Z\",\"temperature\":20.95},{\"date\":\"2013-11-11T08:00:00.000Z\",\"temperature\":21.5},{\"date\":\"2013-11-11T08:30:00.000Z\",\"temperature\":20.94}]}";
		Log.d(TAG, "1.");
		JsonElement jelement;
		JsonObject jobject;
		// Get json from URL
		jelement = new JsonParser().parse(content);
		Log.d(TAG, "2.");
		// Convert json to JsonObject
		jobject = jelement.getAsJsonObject();
		Log.d(TAG, "3.");
		// get "data" json as array
		JsonArray statobj = jobject.getAsJsonArray("data");
		Log.d(TAG, "4.");
		int statsize = statobj.size();

		// Store last entry which will be most recent temp
		jobject = statobj.get(statsize - 1).getAsJsonObject();
		// Remove quotes from string
		String result = jobject.get("date").toString().replaceAll("\"", "");
		Log.d(TAG, "5.");
		SimpleDateFormat iso_date = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.UK);
		// replaceAll required due to some quirk with jodatime and ISO formating
		String date = result.replaceAll("Z", "+0$000");
		Log.d(TAG, "6.");
		try {
			// Convert ISO to Date
			Date time = iso_date.parse(date);
			Log.d(TAG, "6.5.");
			return time;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "7.");
		return null;
		
	}
	public static double getInsideTemp(String content) {
		// {"inside":20.8,"outside":8.9,"weather":"heavy_rain_showers","city":"Childwall"}
		JsonElement jelement;
		JsonObject jobject;
		
		jelement = new JsonParser().parse(content);
		jobject = jelement.getAsJsonObject();
		double inside = Double.parseDouble(jobject.get("inside").toString().replaceAll("\"", "")) ;
		
		//Log.d(TAG,"Inside:" + inside);
		
		return inside;
	}
	
	public static double getOutsideTemp(String content) {
		// {"inside":20.8,"outside":8.9,"weather":"heavy_rain_showers","city":"Childwall"}
		JsonElement jelement;
		JsonObject jobject;
		
		jelement = new JsonParser().parse(content);
		jobject = jelement.getAsJsonObject();
		double outside = Double.parseDouble(jobject.get("outside").toString().replaceAll("\"", "")) ;
		
		//Log.d(TAG,"Outside:" + outside);
		
		return outside;
	}
	
	public static String getWeather(String content) {
		// {"inside":20.8,"outside":8.9,"weather":"heavy_rain_showers","city":"Childwall"}
		JsonElement jelement;
		JsonObject jobject;
		
		jelement = new JsonParser().parse(content);
		jobject = jelement.getAsJsonObject();
		String weather = jobject.get("weather").toString().replaceAll("\"", "") ;
		
		//Log.d(TAG,"Outside:" + outside);
		
		return weather;
	}
	
	public static String getCity(String content) {
		// {"inside":20.8,"outside":8.9,"weather":"heavy_rain_showers","city":"Childwall"}
		JsonElement jelement;
		JsonObject jobject;
		
		jelement = new JsonParser().parse(content);
		jobject = jelement.getAsJsonObject();
		String city = jobject.get("city").toString().replaceAll("\"", "") ;
		
		//Log.d(TAG,"Outside:" + outside);
		
		return city;
	}
	
	public static double getCHTarget(String content) {
		// {"target":4.5}
		JsonElement jelement;
		JsonObject jobject;
		
		jelement = new JsonParser().parse(content);
		jobject = jelement.getAsJsonObject();
		
		double target = Double.parseDouble(jobject.get("target").toString().replaceAll("\"", "")) ;

		Log.d(TAG,"Target:" + target);
		
		return target;
	}
		
	public static Float getLatestTemp(String content) {
		// content =
		// "{\"data\":[{\"date\":\"2013-11-11T00:00:00.000Z\",\"temperature\":21.2},{\"date\":\"2013-11-11T00:30:00.000Z\",\"temperature\":20.99},{\"date\":\"2013-11-11T01:00:00.000Z\",\"temperature\":20.77},{\"date\":\"2013-11-11T01:30:00.000Z\",\"temperature\":20.56},{\"date\":\"2013-11-11T02:00:00.000Z\",\"temperature\":20.37},{\"date\":\"2013-11-11T02:30:00.000Z\",\"temperature\":20.15},{\"date\":\"2013-11-11T03:00:00.000Z\",\"temperature\":20.01},{\"date\":\"2013-11-11T03:30:00.000Z\",\"temperature\":19.81},{\"date\":\"2013-11-11T04:00:00.000Z\",\"temperature\":19.65},{\"date\":\"2013-11-11T04:30:00.000Z\",\"temperature\":19.5},{\"date\":\"2013-11-11T05:00:00.000Z\",\"temperature\":19.43},{\"date\":\"2013-11-11T05:30:00.000Z\",\"temperature\":19.82},{\"date\":\"2013-11-11T06:00:00.000Z\",\"temperature\":20.24},{\"date\":\"2013-11-11T06:30:00.000Z\",\"temperature\":20.47},{\"date\":\"2013-11-11T07:00:00.000Z\",\"temperature\":20.69},{\"date\":\"2013-11-11T07:30:00.000Z\",\"temperature\":20.95},{\"date\":\"2013-11-11T08:00:00.000Z\",\"temperature\":21.5},{\"date\":\"2013-11-11T08:30:00.000Z\",\"temperature\":20.94}]}";
		JsonElement jelement;
		JsonObject jobject;
		
		// Get json from URL
		jelement = new JsonParser().parse(content);

		// Convert json to JsonObject
		jobject = jelement.getAsJsonObject();

		// get "data" json as array
		JsonArray statobj = jobject.getAsJsonArray("data");

		int statsize = statobj.size();

		// Store last entry which will be most recent temp
		jobject = statobj.get(statsize - 1).getAsJsonObject();
		Float result = Float.parseFloat(jobject.get("temperature").toString());
		return result;
	}
	
	public static char getUniCodeWeatherChar(String weather_text) {
		Weather weather ;
		char weather_char ;
		
		weather = Weather.valueOf(weather_text);
		
		switch (weather) {
		case heavy_snow_showers:
			weather_char =  '\uF044';
			break;
		case thundery_showers:
			weather_char =  '\uF046';
			break;
		case thunderstorms:
			weather_char =  '\uF047';
			break;
		case cloudy_with_sleet:
			weather_char =  '\uF040';
			break;
		case sleet_showers:
			weather_char =  '\uF036';
			break;
		case light_snow_showers:
			weather_char =  '\uF035';
			break;
		case light_snow:
			weather_char =  '\uF039';
			break;
		case cloudy_with_heavy_rain:
			weather_char =  '\uF049';
			break;
		case heavy_rain_showers:
			weather_char =  '\uF045';
			break;
		case light_rain_showers:
			weather_char =  '\uF038';
			break;
		case cloudy_with_heavy_snow:
			weather_char =  '\uF048';
			break;
		case cloudy_with_light_snow:
			weather_char =  '\uF039';
			break;
		case cloudy_with_light_rain:
			weather_char =  '\uF043';
			break;
		case fog:
			weather_char =  '\uF050';
			break;
		case mist:
			weather_char =  '\uF033';
			break;
		case black_low_cloud:
			weather_char =  '\uF042';
			break;
		case white_cloud:
			weather_char =  '\uF041';
			break;
		case sunny_intervals:
			weather_char =  '\uF037';
			break;
		case sunny:
			weather_char =  '\uF034';
			break;
		case clear_sky:
			weather_char =  '\uF034';
			break;
		default:
			// Due to enum type we will never get here
			// this check is required in main code
			// Revisit to handle exception within this method
			// Invalid weather code, display *
			weather_char = '\uF053' ;
			break;
		}

		return weather_char ;
	}
}
