package songbook.ax.asu.asu_songbook.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

import songbook.ax.asu.asu_songbook.R;

/**
 * Created by EIS i7 Gamer on 2015-02-26.
 */
public class SongSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = SongSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in milliseconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int SONG_NOTIFICATION_ID = 3004;

    public SongSyncAdapter(Context context, boolean autoInitialize){
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String songJsonStr = null;
        String format = "json";
        String units = "metric";
        int numDays = 14;
        // Construct the URL for the OpenWeatherMap query
        // Possible parameters are avaiable at OWM's forecast API page, at
        // http://openweathermap.org/API#forecast
        final String SONG_BASE_URL = "83.255.37.175/songbook/dbtest.php?";
        final String ID_PARAM = "song";
        final String TIME_PARAM = "timestamp";
        final String EVENT_PARAM = "event";

        Uri builtUri;

        try {
            String timestamp;
            String event;

            builtUri = Uri.parse(SONG_BASE_URL).buildUpon()
                    .appendQueryParameter(ID_PARAM, "0")
                    //.appendQueryParameter(TIME_PARAM, timestamp)
                    //.appendQueryParameter(EVENT_PARAM,event)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {

                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {

                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {

                // Stream was empty. No point in parsing.
                return;
            }
            songJsonStr = buffer.toString();

            getSongDataFromJson(songJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet. If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {
            /*
            * Add the account and account type, no password or user data
            * If successful, return the Account object, otherwise report an error.
            */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
            * If you don't set android:syncable="true" in
            * in your <provider> element in the manifest,
            * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
            * here.
            */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        /*
        * Since we've created an account
        */
        SongSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
        * Without calling setSyncAutomatically, our periodic sync will not be enabled.
        */
        ContentResolver.setSyncAutomatically(newAccount, "context.getString(R.string.content_authority)", true);

        /*
        * Finally, let's do a sync to get things started
        */
        syncImmediately(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = "context.getString(R.string.content_authority)";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private void getSongDataFromJson(String jsonStr)throws JSONException {
        Log.v(LOG_TAG,jsonStr);

        Random rand = new Random();
        Date date = new Date();
        Vector<ContentValues> cVVector = new Vector<ContentValues>(2);

        ContentValues songValues = new ContentValues();
        songValues.put(SongContract.SongTable.COLUMN_SONG_ID, rand.nextInt());
        songValues.put(SongContract.SongTable.COLUMN_LAST_UPADTED, date.getTime());
        songValues.put(SongContract.SongTable.COLUMN_SONG_NAME,"Testvisan");
        songValues.put(SongContract.SongTable.COLUMN_SONG_MELODY,"Ritch ratch");
        songValues.put(SongContract.SongTable.COLUMN_TEXT,jsonStr);

        cVVector.add(songValues);

        int inserted = 0;

        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContext().getContentResolver().bulkInsert(SongContract.SongTable.CONTENT_URI, cvArray);
            //getContext().getContentResolver().insert(SongContract.SongTable.CONTENT_URI,cvArray[0]);
            // delete old data so we don't build up an endless history
            //getContext().getContentResolver().delete(SongContract.SongTable.CONTENT_URI,
             //       SongContract.SongTable.COLUMN_DATE + " <= ?",
            //        new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});

        }

/*

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy: constructor takes the JSON string and converts it
        // into an Object hierarchy for us.
        // These are the names of the JSON objects that need to be extracted.
        // Location information
        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";

        // Location coordinate
        final String OWM_LATITUDE = "lat";
        final String OWM_LONGITUDE = "lon";

        // Weather information. Each day's forecast info is an element of the "list" array.
        final String OWM_LIST = "list";
        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";

        // All temperatures are children of the "temp" object.
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";
        try {
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
            String cityName = cityJson.getString(OWM_CITY_NAME);
            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
            double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);
            long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

            // Insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.
            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.
            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();
            for (int i = 0; i < weatherArray.length(); i++) {

                // These are the values that will be collected.
                long dateTime;
                double pressure;
                int humidity;
                double windSpeed;
                double windDirection;
                double high;
                double low;
                String description;
                int weatherId;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                pressure = dayForecast.getDouble(OWM_PRESSURE);
                humidity = dayForecast.getInt(OWM_HUMIDITY);
                windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

                // Description is in a child array called "weather", which is 1 element long.
                // That element also contains a weather code.
                JSONObject weatherObject =
                        dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);
                weatherId = weatherObject.getInt(OWM_WEATHER_ID);

                // Temperatures are in a child object called "temp". Try not to name variables
                // "temp" when working with temperature. It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                high = temperatureObject.getDouble(OWM_MAX);
                low = temperatureObject.getDouble(OWM_MIN);
                ContentValues weatherValues = new ContentValues();
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);
                cVVector.add(weatherValues);
            }
            int inserted = 0;

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, cvArray);

                // delete old data so we don't build up an endless history
                getContext().getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
                        WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
                        new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});

            }
            Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
*/
    }
}
