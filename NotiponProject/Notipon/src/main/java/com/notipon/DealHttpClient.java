package com.notipon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jayzeng on 11/2/13.
 */
public class DealHttpClient {
    private static String IMG_URL = "http://img.grouponcdn.com/deal/";

    public Bitmap downloadImage(String deal) {
        HttpURLConnection connection = null;
        InputStream is = null;

        try {
            Log.d("Notipon", IMG_URL + deal);
            URL imageUrl = new URL(IMG_URL + deal);
            Bitmap image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            return image;
        } catch (Throwable e) {
            Log.d("Notipon", "failed to write to buffer");
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable e) {

            }

            try {
                connection.disconnect();
            } catch (Throwable e) {

            }
        }
        return null;
    }
}
