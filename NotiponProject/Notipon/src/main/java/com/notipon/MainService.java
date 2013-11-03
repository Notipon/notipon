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
import java.util.ArrayList;

/**
 * Created by keith on 11/2/13.
 */
public class MainService extends Service  {
    public static final String TAG = "MainService";
    public static final String BROADCAST_ACTION = "com.notipon.MainService.broadcast";
    public static final String PACKAGE_NAME = "com.notipon";
    private static final String DEALS_EXTRA = "com.notipon.deals";
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
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkForResults() {
        Log.d(TAG, "Checking for results...");
        // get filter
        SharedPreferences settings = getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE);
        Filter filter = Filter.getActiveFilter(settings);

        // DEBUG for filter
        if (!filter.isEmpty()){
            Log.d(TAG, filter.name);
            Log.d(TAG, filter.location);
        }
        else {
            Log.d(TAG, "No filter found :(");
        }

        // run the search and filter
        if (!filter.isEmpty()) {
            return;
        }
        ArrayList<Deal> deals = getDeals(filter);

        // DEBUG
        for (Deal deal : deals) {
            Log.d(TAG, "Found a deal!");
            Log.d(TAG, "\t" + deal.merchantName);
            Log.d(TAG, "\t" + deal.dealUrl);
            Log.d(TAG, "\t" + deal.imageUrl);
            for (String area : deal.areas) {
                Log.d(TAG, "\t" + area);
            }
        }

        // call notification if match(es) found
        sendDeals(deals);
    }

    private void sendDeals(ArrayList<Deal> deals) {
        Intent intent = new Intent(NotifyReceiver.INTENT_NAME);
        intent.putExtra(DEALS_EXTRA, deals);
        sendBroadcast(intent);
    }

    private ArrayList<Deal> getDeals() {
        ArrayList<Deal> examples = new ArrayList<Deal>();
        examples.add(new Deal("Caffe Vita", "http://www.caffevita.com/", "http://www.chefscollaborative.org/wp-content/uploads/2010/08/caffe-vita-logo-bw-650x1024.jpg", "Seattle"));
        examples.add(new Deal("Thai Curry Simple", "http://www.thaicurrysimple.com/", "http://www.thestranger.com/binary/c21d/Thai_Curry_Simple_Dominic_Holden.jpg", "Seattle"));
        return examples;
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

                    JSONObject merchantObject = deal.getJSONObject("merchant");
                    if (merchantObject != null) {
                        parsed.merchantName = merchantObject.getString("name");
                    }
                    parsed.imageUrl = deal.getString("mediumImageUrl");

                    JSONArray locations = deal.getJSONArray("areas");
                    if (locations != null) {
                        for (int j = 0; j < locations.length(); j++) {
                            parsed.areas.add(locations.getJSONObject(i).getString("name"));
                        }
                    }
                }
            }

            return deals;
        } catch (IOException e) {
            Log.e(TAG, "Failed to load example Json");
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse example Json");
        }

        return null;
    }

    private ArrayList<Deal> getDeals(Filter filter) {
        return filter.apply(getDeals());
    }
}
