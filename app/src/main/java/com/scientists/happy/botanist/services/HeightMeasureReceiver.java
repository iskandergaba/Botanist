// Handle notifications to measure height
// @author: Iskander Gaba
package com.scientists.happy.botanist.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.scientists.happy.botanist.ui.LoginActivity;

import static android.content.Context.NOTIFICATION_SERVICE;
public class HeightMeasureReceiver extends BroadcastReceiver {
    /**
     * Notification request received
     * @param context - calling context
     * @param intent - calling intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getExtras().getString("name");
        String plantId = intent.getExtras().getString("plant_id");
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_botanist_big);
        int notificationId = intent.getExtras().getInt("id");
        Intent resultIntent = new Intent(context, LoginActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_ruler_notification)
                        .setLargeIcon(largeIcon)
                        .setDefaults(Notification.DEFAULT_SOUND).setContentTitle("Check " + name + " Height")
                        .setContentText("Keep track of " + name + " height record").setAutoCancel(true)
                        .setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(notificationId, mBuilder.build());
        DatabaseManager.getInstance().updateNotificationTime(plantId, "lastMeasureNotification");
    }
}