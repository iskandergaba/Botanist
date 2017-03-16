package com.scientists.happy.botanist.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.ui.LoginActivity;

import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

public class BirthdayReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String name = intent.getExtras().getString("name");
        String species = intent.getExtras().getString("species");
        long birthday = intent.getExtras().getLong("birthday");
        int notificationId = intent.getExtras().getInt("id");

        int ageInYears = getAgeInYears(birthday);

        Intent resultIntent = new Intent(context, LoginActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentTitle("It's " + name + "'s birthday!")
                        .setContentText(name + " (" + species + ") turns " + ageInYears + " years today!")
                        .setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(notificationId, mBuilder.build());
    }

    private int getAgeInYears(long birthday) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(birthday);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(System.currentTimeMillis());

        return cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
    }
}