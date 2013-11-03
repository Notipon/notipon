package com.notipon;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by john-gu on 11/2/13.
 */
public class NotifyReceiver extends BroadcastReceiver {
    public static final String INTENT_NAME = "com.notipon.NOTIFY";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Notipon", "Receiver got com.notipon.NOTIFY intent, time to notify");

        // Check that we received right intent and show notifications
        if (INTENT_NAME.equals(intent.getAction())) {
            Log.d("Notipon", "passed intent check. notify on " + intent.toString());
            ArrayList<Deal> deals = (ArrayList<Deal>) intent.getSerializableExtra(MainService.DEALS_EXTRA);
            if (deals == null || deals.size() < 1) {
                Log.d("NotifyReceiver", "No deals found");
                return;
            }

            Log.d("NotifyReceiver", "Listing deals");
            for (Deal deal : deals) {
                Log.d("NotifyReceiver", "Deal found: " + deal.merchantName);
            }

            Deal activeDeal = deals.get(0);

            Notification noti = new Notification.Builder(context)
                    .setContentTitle("Notipon")
                    .setContentText(activeDeal.merchantName)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .build();


            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, noti);
        }
    }
}
