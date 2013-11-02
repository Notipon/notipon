package com.notipon;

import android.content.SharedPreferences;

/**
 * Created by ryan on 11/2/13.
 */

public class Filter {
    public String name;
    public String location;

    private static final String NAME = "name";
    private static final String LOCATION = "location";

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
}
