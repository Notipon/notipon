package com.notipon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.notipon.Deal;
import com.notipon.Filter;

public class DealFetcher {
    private static final String EXAMPLE_JSON_FILE = "example_deals.json";
    private static final String TAG = "DealFetcher";
    private Context context;

    public DealFetcher(Context context) {
        this.context = context;
    }

    public ArrayList<Deal> getDeals() {
        return getJsonExampleDeals();
    }

    public ArrayList<Deal> getDeals(Filter filter) {
        return filter.apply(getDeals());
    }

    private ArrayList<Deal> getJsonExampleDeals() {
        return Deal.parseJsonDeals(loadExampleJson());
    }

    private String loadExampleJson() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(context.getAssets().open(EXAMPLE_JSON_FILE)));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            in.close();

            return builder.toString();
        } catch (IOException e) {
            Log.e(TAG, "Failed to load example Json");
        }

        return "";
    }
}