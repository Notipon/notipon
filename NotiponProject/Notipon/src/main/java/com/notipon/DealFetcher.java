package com.notipon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.notipon.Deal;
import com.notipon.Filter;

public class DealFetcher {
    private static final String EXAMPLE_JSON_FILE = "example_deals.json";
    private static final String TAG = "DealFetcher";
    private Context context;
    private static final String grouponKey = "8a42c4f943043ffe9cb59defb3f51bc30e4c59a3";

    public DealFetcher(Context context) {
        this.context = context;
    }

    public ArrayList<Deal> getDeals() {
        //return Deal.parseJsonDeals(fetchCurrentDeals());
        return Deal.parseJsonDeals(loadExampleJson());
    }

    public ArrayList<Deal> getDeals(Filter filter) {
        return filter.apply(getDeals());
    }

    private String loadStreamAsString(InputStream binaryInput) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(binaryInput));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            builder.append(line);
        }
        in.close();

        return builder.toString();
    }

    private String loadExampleJson() {
        try {
            return loadStreamAsString(context.getAssets().open(EXAMPLE_JSON_FILE));
        } catch (IOException e) {
            Log.e(TAG, "Failed to load example Json");
        }

        return "";
    }
    
    private URL buildDealRequestUrl() throws MalformedURLException {
        return new URL("http://api.groupon.com/v2/deals/?client_id=" + grouponKey);
    }

    /**
     * Testing: Tested that it works.  But we need to be careful to throttle
     * requests or else it'll get out key blocked.  Also, disabling it for the
     * demo.
     */
    private String fetchCurrentDeals() {
        try {
            // TODO: Check the HTTP response code, the response content
            URL dealUrl = buildDealRequestUrl();
            return loadStreamAsString(dealUrl.openStream());
        }
        catch (MalformedURLException e) {
            Log.e(TAG, "Bad deal URL:", e);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to load URL:", e);
        }
	
        Log.e(TAG, "Error in fetching current deals, falling back to example Json.");
        return loadExampleJson();
    }
}