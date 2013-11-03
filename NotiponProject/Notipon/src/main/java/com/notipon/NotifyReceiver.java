package com.notipon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.notipon.Deal;
import com.notipon.MainService;

/**
 * Created by john-gu on 11/2/13.
 */
public class NotifyReceiver extends BroadcastReceiver {
    public static final String TAG = "NotifyReceiver";
    public static final String INTENT_NAME = "com.notipon.NOTIFY";

    private static final String HISTORY = "history";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // Check that we received right intent and show notifications
        if (INTENT_NAME.equals(intent.getAction())) {
            new Thread() {
                @Override
                public void run() {
                    ArrayList<Deal> deals = (ArrayList<Deal>) intent.getSerializableExtra(MainService.DEALS_EXTRA);
                    if (deals == null || deals.size() < 1) {
                        Log.d(TAG, "No deals found");
                        return;
                    }

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

                    // Pending intent for click is redirection using the deep-link
                    Intent resultIntent = new Intent(Intent.ACTION_VIEW);
                    resultIntent.setData(Uri.parse(activeDeal.dealUrl));
                    PendingIntent openUrlIntent = PendingIntent.getActivity(context, activeDeal.dealId, resultIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

                    Notification.Builder builder = new Notification.Builder(context)
                            .setContentTitle(context.getResources().getString(R.string.deal_found))
                            .setContentText(activeDeal.merchantName)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentIntent(openUrlIntent)
                            .setAutoCancel(true);

                    try {
                        URL imageUrl = new URL(activeDeal.imageUrl);
                        Bitmap origImg = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                        Bitmap cropImg;

                        int origWidth = origImg.getWidth();
                        int origHeight = origImg.getHeight();

                        int notiWidth = (int)context.getResources().getDimension(android.R.dimen.notification_large_icon_width);
                        int notiHeight = (int)context.getResources().getDimension(android.R.dimen.notification_large_icon_height);

                        // If image smaller than notification icon, scale up and crop center
                        if (origWidth < notiWidth || origHeight < notiHeight) {
                            float scale;
                            if (origWidth < origImg.getHeight()) {
                                scale = notiWidth / (float)origWidth;
                            } else {
                                scale = notiHeight / (float)origHeight;
                            }

                            int scaledWidth = (int)(origWidth * scale);
                            int scaledHeight = (int)(origHeight * scale);

                            Bitmap scaled = Bitmap.createScaledBitmap(origImg, scaledWidth, scaledHeight, false);

                            int offsetX = (scaledWidth - notiWidth) / 2;
                            int offsetY = (scaledHeight - notiHeight) / 2;
                            cropImg = Bitmap.createBitmap(scaled, offsetX, offsetY, notiWidth, notiHeight);
                        } else {
                            // Use original image if it's larger than notification icon size
                            cropImg = origImg;
                        }

                        builder.setLargeIcon(cropImg);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Set big picture
                    URL lrgImageUrl = null;
                    Bitmap bigPicture = null;
                    try {
                        lrgImageUrl = new URL(activeDeal.lrgImageUrl);
                        bigPicture = BitmapFactory.decodeStream(lrgImageUrl.openConnection().getInputStream());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Notification noti;
                    if (lrgImageUrl != null) {
                        Notification.BigPictureStyle bigNoti = new Notification.BigPictureStyle(builder);
                        bigNoti.bigPicture(bigPicture);
                        bigNoti.setSummaryText(activeDeal.merchantName);
                        noti = bigNoti.build();
                    } else {
                        noti = builder.build();
                    }

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(activeDeal.dealId, noti);

                    recordNotification(context, activeDeal);
                }
            }.start();
        }
    }

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
