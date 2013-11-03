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

import com.notipon.Deal;
import com.notipon.DealFetcher;
import com.notipon.Filter;
import com.notipon.NotifyReceiver;

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
public class MainService extends Service {
    public static final String TAG = "MainService";
    public static final String BROADCAST_ACTION = "com.notipon.MainService.broadcast";
    public static final String PACKAGE_NAME = "com.notipon";
    public static final String DEALS_EXTRA = "com.notipon.deals";
    private Handler handler = new Handler();
    private Intent intent;
    private static final int DELAY_MS = 5000;
    private Runnable updateRunnable;

    private DealFetcher fetcher;

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
        fetcher = new DealFetcher(this);
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
        SharedPreferences settings = getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE);
        final Filter filter = Filter.getActiveFilter(settings);

        if (filter.isEmpty()) {
            return;
        }

        // need to do network off the main thread
        new Thread() {
            public void run() {
                Log.d(TAG, "Filter: " + filter);

                // search and filter deals
                ArrayList<Deal> deals = fetcher.getDeals();

                // DEBUG
                logDealList("Unfiltered deals", deals);

                deals = filter.apply(deals);

                // DEBUG
                logDealList("Filtered deals", deals);

                // call notification
                sendDeals(deals);
            }
        }.start();
    }

    private void sendDeals(ArrayList<Deal> deals) {
        Intent intent = new Intent(NotifyReceiver.INTENT_NAME);
        intent.putExtra(DEALS_EXTRA, deals);
        sendBroadcast(intent);
    }

    public static void logDealList(String header, ArrayList<Deal> deals) {
        Log.d(TAG, header + ":");
        if (deals == null) {
            Log.d(TAG, "null");
            return;
        }
        for (Deal deal : deals) {
            Log.d(TAG, "Deal: " + deal);
        }
    }
}
