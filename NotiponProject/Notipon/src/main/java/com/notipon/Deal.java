package com.notipon;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ryan on 11/2/13.
 */
public class Deal implements Serializable {
    private static final String TAG = "Deal";
    public String merchantName;
    public String dealUrl;
    public String imageUrl;
    public String lrgImageUrl;
    public ArrayList<String> areas;
    public ArrayList<String> categories;
    public Date endTime;
    boolean isSoldOut;
    public Integer dealId;
    public Bitmap imgData;

    public Deal() {
        areas = new ArrayList<String>();
        categories = new ArrayList<String>();
    }

    public Deal(Deal other) {
        merchantName = other.merchantName;
        dealUrl = other.dealUrl;
        imageUrl = other.imageUrl;
        lrgImageUrl = other.lrgImageUrl;
        areas = new ArrayList<String>(other.areas);
        categories = new ArrayList<String>(other.categories);
        endTime = other.endTime;
        isSoldOut = other.isSoldOut;
        computeDealID();
    }

    public void computeDealID() {
        // unique deal ID to prevent re-notifying users about the same deal
        dealId = merchantName.hashCode() ^ dealUrl.hashCode() ^ imageUrl.hashCode();
        // include end time in ID if possible, so later deals for the same
        // merchant can be displayed
        if (endTime != null) {
            dealId ^= endTime.hashCode();
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(merchantName);
        builder.append(' ');
        builder.append(dealUrl);
        builder.append(" areas(");
        for (String area : areas) {
            builder.append(' ');
            builder.append(area);
        }
        builder.append(") ");
        builder.append(" categories(");
        for (String category : categories) {
            builder.append(' ');
            builder.append(category);
        }
        builder.append(") ");
        if (endTime != null) {
            builder.append(endTime);
        }
        builder.append(' ');
        builder.append(isSoldOut);

        return builder.toString();
    }

    public static ArrayList<Deal> parseJsonDeals(String jsonText) {
        try {
            JSONObject json = new JSONObject(jsonText);
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
                    parsed.lrgImageUrl = deal.getString("largeImageUrl");

                    JSONArray locations = deal.getJSONArray("areas");
                    if (locations != null) {
                        for (int j = 0; j < locations.length(); j++) {
                            parsed.areas.add(locations.getJSONObject(j).getString("name"));
                        }
                    }

                    JSONArray tags = deal.getJSONArray("tags");
                    if (tags != null) {
                        for (int j = 0; j < tags.length(); j++) {
                            parsed.categories.add(tags.getJSONObject(j).getString("name"));
                        }
                    }

                    parsed.computeDealID();
                    deals.add(parsed);
                }
            }

            return deals;
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse example Json", e);
        }

        return null;
    }

    private static Date convertToDate(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        try {
            return format.parse(date);
        } catch (Exception e) {
        }
        return null;
    }
}
