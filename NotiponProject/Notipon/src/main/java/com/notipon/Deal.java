package com.notipon;

import java.util.ArrayList;

/**
 * Created by ryan on 11/2/13.
 */
public class Deal {
    public String merchantName;
    public String dealUrl;
    public String imageUrl;
    public ArrayList<String> areas;

    public Deal(String merchantName, String dealUrl, String imageUrl, String area) {
        this.merchantName = merchantName;
        this.dealUrl = dealUrl;
        this.imageUrl = imageUrl;
        this.areas = new ArrayList<String>();
        this.areas.add(area);
    }
}
