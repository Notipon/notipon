package com.notipon;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by john-gu on 11/2/13.
 */
public class NotifyReceiver extends BroadcastReceiver {
    public static final String INTENT_NAME = "com.notipon.NOTIFY";

    private static final String HISTORY = "history";


    private static AtomicInteger mNotiCounter = new AtomicInteger(0);


    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("Notipon", "Receiver got com.notipon.NOTIFY intent, time to notify");

        // Check that we received right intent and show notifications
        if (INTENT_NAME.equals(intent.getAction())) {
            Log.d("Notipon", "passed intent check. notify on " + intent.toString());
            new Thread() {
                @Override
                public void run() {
                    ArrayList<Deal> deals = (ArrayList<Deal>) intent.getSerializableExtra(MainService.DEALS_EXTRA);
                    if (deals == null || deals.size() < 1) {
                        Log.d("NotifyReceiver", "No deals found");
                        return;
                    }

                    Log.d("NotifyReceiver", "Listing deals");
                    for (Deal deal : deals) {
                        Log.d("NotifyReceiver", "Deal found: " + deal.merchantName);
                    }

                    // TODO log messages so we know this is working
                    Deal activeDeal = null;
                    for (Deal deal : deals) {
                        if (!alreadyNotified(context, deal.dealId)) {
                            activeDeal = deal;
                            break;
                        }
                    }
                    if (activeDeal == null) {
                        return;
                    }

                    int notiNum = mNotiCounter.incrementAndGet();
                    Notification.Builder builder = new Notification.Builder(context)
                            .setContentTitle("Notipon")
                            .setContentText(activeDeal.merchantName)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setNumber(notiNum);


                    try {
                        Log.d("NotifyReceiver", "Setting image to " + activeDeal.imageUrl);
                        URL imageUrl = new URL(activeDeal.imageUrl);
                        Bitmap image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                        builder.setLargeIcon(image);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Notification noti = builder.build();


            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, noti);

            recordNotification(context, activeDeal);
        }
    }.start();
        }}

    public boolean alreadyNotified(Context context, Integer dealId) {
        SharedPreferences settings = context.getSharedPreferences(MainService.PACKAGE_NAME, context.MODE_PRIVATE);
        Set<String> history = settings.getStringSet(HISTORY, new HashSet<String>());
        if (history.contains(dealId.toString())) {
            return true;

        }
        return false;
    }

    public void recordNotification(Context context, Deal deal) {
        SharedPreferences settings = context.getSharedPreferences(MainService.PACKAGE_NAME, context.MODE_PRIVATE);
        Set<String> history = settings.getStringSet(HISTORY, new HashSet<String>());
        history.add(deal.dealId.toString());
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(HISTORY, history);
        editor.commit();
    }
}
