package com.notipon;

import android.content.SharedPreferences;

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

    public static Filter getActiveFilter(SharedPreferences settings) {
        Filter filter = new Filter();
        filter.name = settings.getString(NAME, null);
        filter.location = settings.getString(LOCATION, null);
        return filter;
    }

    public void setActiveFilter(SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(NAME, name);
        editor.putString(LOCATION, location);
        editor.commit();
    }

    public boolean matches(Deal deal) {
        boolean inArea = false;
        for (String area : deal.areas) {
            if (area.equalsIgnoreCase(location)) {
                inArea = true;
                break;
            }
        }
        // TODO better fuzzy searching
        if (inArea && deal.merchantName.equalsIgnoreCase(name)) {
            return true;
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
}
