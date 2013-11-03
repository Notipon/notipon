package com.notipon;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ryan on 11/2/13.
 */
public class Deal implements Serializable {
    public String merchantName;
    public String dealUrl;
    public String imageUrl;
    public ArrayList<String> areas;
    public String endTime;
    boolean isSoldOut;
    public Integer dealId;
    public byte[] imgData;

    public Deal(String merchantName, String dealUrl, String imageUrl, String area, String endTime, boolean isSoldOut) {
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
        builder.append(endTime);
        builder.append(' ');
        builder.append(isSoldOut);

        return builder.toString();
    }
}
