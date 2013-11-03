package com.notipon;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ryan on 11/2/13.
 */
public class Deal implements Serializable {
    public String merchantName;
    public String dealUrl;
    public String imageUrl;
    public ArrayList<String> areas;
    public Date endTime;
    boolean isSoldOut;
    public Integer dealId;
    public Bitmap imgData;

    public Deal(String merchantName, String dealUrl, String imageUrl, String area, Date endTime, boolean isSoldOut) {
        this.merchantName = merchantName;
        this.dealUrl = dealUrl;
        this.imageUrl = imageUrl;
        this.areas = new ArrayList<String>();
        this.areas.add(area);
        this.endTime = endTime;
        this.isSoldOut = isSoldOut;
        computeDealID();
    }

    public Deal() {
        areas = new ArrayList<String>();
    }

    public Deal(Deal parsed) {
        merchantName = parsed.merchantName;
        dealUrl = parsed.dealUrl;
        imageUrl = parsed.imageUrl;
        areas = new ArrayList<String>(parsed.areas);
        endTime = parsed.endTime;
        isSoldOut = parsed.isSoldOut;
        computeDealID();
    }

    public void computeDealID() {
        dealId = merchantName.hashCode() ^ dealUrl.hashCode() ^ imageUrl.hashCode();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(merchantName);
        builder.append(' ');
        builder.append(dealUrl);
        for (String area : areas) {
            builder.append(' ');
            builder.append(area);
        }
        builder.append(' ');
        if (endTime != null) {
            builder.append(endTime);
        }
        builder.append(' ');
        builder.append(isSoldOut);

        return builder.toString();
    }
}
