package com.notipon;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ryan on 11/2/13.
 */

public class Filter {
    public String name;
    public String location;

    private static final String NAME = "name";
    private static final String LOCATION = "location";

    public boolean isEmpty() {
        if (name == null || location == null) {
            return true;
        }
        return false;
    }

    public Filter(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Filter() {
    }

    public static Filter getActiveFilter(SharedPreferences settings) {
        Filter filter = new Filter();
        filter.name = settings.getString(NAME, null);
        filter.location = settings.getString(LOCATION, null);
        return filter;
    }

    public void setActiveFilter(SharedPreferences settings) {
        normalizeFields();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(NAME, name);
        editor.putString(LOCATION, location);
        editor.commit();
    }

    private void normalizeFields() {
        if (name != null) {
            name = name.trim();
        }

        if (location != null) {
            location = location.trim();
        }
    }

    public boolean matches(Deal deal) {
        boolean inArea = false;
        if (location.length() == 0) {
            inArea = true;
        }
        else {
            for (String area : deal.areas) {
                if (area.equalsIgnoreCase(location)) {
                    inArea = true;
                    break;
                }
            }
        }

        // TODO better fuzzy searching
        if (!deal.isSoldOut && inArea) {
            String filteredName = filterStopWords(name);
            // match on merchant name
            if (filterStopWords(deal.merchantName).equalsIgnoreCase((filteredName))) {
                return true;
            }
            // match on category name
            for (String category : deal.categories) {
                if (filterStopWords(category).equalsIgnoreCase((filteredName))) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Deal> apply(ArrayList<Deal> deals) {
        ArrayList<Deal> newDeals = new ArrayList<Deal>();
        for (Deal deal : deals) {
            if (matches(deal)) {
                newDeals.add(deal);
            }
        }
        return newDeals;
    }

    public String filterStopWords(String name) {
        Log.d("", "original word: " + name);
        String stopWords = "(\\b(a|an|and|in|on|of)\\b|\\&)";
        String res = name.replaceAll(stopWords, "");
        res = res.replaceAll("\\s+", " ");
        Log.d("", "new word: " + res);
        return res;
    }

    public String toString() {
        return "(name=" + name + ", location=" + location + ")";
    }
}
