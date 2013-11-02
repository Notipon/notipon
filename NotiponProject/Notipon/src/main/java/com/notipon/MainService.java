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
        // get filter
        SharedPreferences settings = getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE);
        Filter filter = Filter.getActiveFilter(settings);
        // run the search
        ArrayList<Deal> deals = getDeals(filter);
        // apply filter
        // call notification if match(es) found
    }

    private ArrayList<Deal> getDeals() {
        return null;
    }

    private ArrayList<Deal> getDeals(Filter filter) {
        return filter.apply(getDeals());
    }
}
