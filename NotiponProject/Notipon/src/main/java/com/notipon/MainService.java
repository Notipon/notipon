package com.notipon;

import android.app.Service;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by keith on 11/2/13.
 */
public class MainService extends Service  {
    public static final String TAG = "MainService";
    public static final String BROADCAST_ACTION = "com.notipon.MainService.broadcast";
    public static final String PACKAGE_NAME = "com.notipon";
    public static final String DEALS_EXTRA = "com.notipon.deals";
    private static final String EXAMPLE_JSON_FILE = "example_deals.json";
    private Handler handler = new Handler();
    private Intent intent;
    private static final int DELAY_MS = 5000;
    private Runnable updateRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Created service!");
        updateRunnable = new Runnable() {
            public void run() {
                // call the actual update function
                checkForResults();
                handler.postDelayed(this, DELAY_MS);
            }
        };
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
            handler.postDelayed(updateRunnable, DELAY_MS);
        }
        else {
            Log.e(TAG, "updateRunnable is null");
        }
        Log.d(TAG, "Started service!");

        // test the Json parsing
        logDealList("Json deals", getJsonExampleDeals());

        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkForResults() {
        SharedPreferences settings = getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE);
        Filter filter = Filter.getActiveFilter(settings);

        if (filter.isEmpty()) {
            return;
        }

        Log.d(TAG, filter.name);
        Log.d(TAG, filter.location);

        // search and filter deals
        ArrayList<Deal> deals = getDeals(filter);

        // DEBUG
        logDealList("Loaded deals", deals);

        // call notification
        sendDeals(deals);
    }

    private void sendDeals(ArrayList<Deal> deals) {
        Intent intent = new Intent(NotifyReceiver.INTENT_NAME);
        intent.putExtra(DEALS_EXTRA, deals);
        sendBroadcast(intent);
    }

    private ArrayList<Deal> getDeals() {
        return getJsonExampleDeals();
    }

    private ArrayList<Deal> getJsonExampleDeals() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(getAssets().open(EXAMPLE_JSON_FILE)));
            StringBuilder builder = new StringBuilder();
            String line;

            while ( (line = in.readLine()) != null) {
                builder.append(line);
            }
            in.close();

            JSONObject json = new JSONObject(builder.toString());
            JSONArray dealArray = json.getJSONArray("deals");

            ArrayList<Deal> deals = new ArrayList<Deal>();
            if (dealArray != null) {
                for (int i = 0; i < dealArray.length(); i++) {
                    JSONObject deal = dealArray.getJSONObject(i);

                    Deal parsed = new Deal();
                    parsed.dealUrl = deal.getString("dealUrl");
                    parsed.endTime = convertToDate(deal.getString("endAt"));
                    parsed.isSoldOut = deal.getBoolean("isSoldOut");

                    JSONObject merchantObject = deal.getJSONObject("merchant");
                    if (merchantObject != null) {
                        parsed.merchantName = merchantObject.getString("name");
                    }
                    parsed.imageUrl = deal.getString("mediumImageUrl");

                    JSONArray locations = deal.getJSONArray("areas");
                    if (locations != null) {
                        for (int j = 0; j < locations.length(); j++) {
                            parsed.areas.add(locations.getJSONObject(j).getString("name"));
                        }
                    }

                    parsed.computeDealID();
                    deals.add(parsed);
                }
            }

            return deals;
        } catch (IOException e) {
            Log.e(TAG, "Failed to load example Json");
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse example Json", e);
        }

        return null;
    }

    private void logDealList(String header, ArrayList<Deal> deals) {
        Log.d(TAG, header + " deals:");
        if (deals == null) {
            Log.d(TAG, "null");
            return;
        }
        for (Deal deal : deals) {
            Log.d(TAG, "Deal: " + deal);
        }
    }

    private ArrayList<Deal> getDeals(Filter filter) {
        return filter.apply(getDeals());
    }

    private Date convertToDate(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        try {
            return format.parse(date);
        }
        catch (Exception e) {
        }
        return null;
    }
}
