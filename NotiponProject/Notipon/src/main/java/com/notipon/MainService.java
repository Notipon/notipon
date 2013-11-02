package com.notipon;

import android.app.Service;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by keith on 11/2/13.
 */
public class MainService extends Service  {
    public static final String TAG = "MainService";
    public static final String BROADCAST_ACTION = "com.notipon.MainService.broadcast";
    public static final String PACKAGE_NAME = "com.notipon";
    private static final String DEALS_EXTRA = "com.notipon.deals";
    private Handler handler = new Handler();
    private Intent intent;
    private static final int DELAY_MS = 5000;
    private Runnable updateRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
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
        if (updateRunnable == null) {
            handler.removeCallbacks(updateRunnable);
            handler.postDelayed(updateRunnable, DELAY_MS);
        }
        else {
            Log.e(TAG, "updateRunnable is null");
        }

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

        // run the search and filter
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
        Intent intent = new Intent("whatever John says here");
        intent.putExtra(DEALS_EXTRA, deals);
        sendBroadcast(intent);
    }

    private ArrayList<Deal> getDeals() {
        ArrayList<Deal> examples = new ArrayList<Deal>();
        examples.add(new Deal("Caffe Vita", "http://www.caffevita.com/", "http://www.chefscollaborative.org/wp-content/uploads/2010/08/caffe-vita-logo-bw-650x1024.jpg", "Seattle"));
        examples.add(new Deal("Thai Curry Simple", "http://www.thaicurrysimple.com/", "http://www.thestranger.com/binary/c21d/Thai_Curry_Simple_Dominic_Holden.jpg", "Seattle"));
        return examples;
    }

    private ArrayList<Deal> getDeals(Filter filter) {
        return filter.apply(getDeals());
    }
}
