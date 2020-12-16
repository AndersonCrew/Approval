package com.crewcloud.apps.crewapproval.firebase;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.crewcloud.apps.crewapproval.CrewCloudApplication;
import com.crewcloud.apps.crewapproval.R;
import com.crewcloud.apps.crewapproval.activity.MainActivity;
import com.crewcloud.apps.crewapproval.util.Constants;
import com.crewcloud.apps.crewapproval.util.PreferenceUtilities;
import com.crewcloud.apps.crewapproval.util.TimeUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.support.v4.app.NotificationCompat.PRIORITY_LOW;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    int notifyID = 1;
    String CHANNEL_ID = "CrewApproval_channel_01";
    CharSequence name = "CrewApproval";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Map<String, String> mapData = remoteMessage.getData();


        int badgeCount = 0;
        try {
            long lastTimePush = CrewCloudApplication.getInstance().getPreferenceUtilities().getLongValue(Constants.TIME_LAST_PUSH, 0);
            if (mapData.containsKey("time")) {
                long timePush = Long.parseLong(mapData.get("time"));
                Log.d("Notification", "lastTimePush " + lastTimePush + "\n" + "timePush " + timePush + "\n");
                if (timePush < lastTimePush) {
                    Log.d("Notification", "return");
                } else {
                    Log.d("Notification", "putIntValue");
                    CrewCloudApplication.getInstance().getPreferenceUtilities().putLongValue(Constants.TIME_LAST_PUSH, timePush);
                }
            }

            String value = mapData.get("badgecount");
            Log.d("Notification", "Unread  " + value);

            if (value != null) {
                badgeCount = Integer.parseInt(value);
                if (badgeCount > 0) {
                    CrewCloudApplication.getInstance().shortcut(badgeCount);
                } else {
                    CrewCloudApplication.getInstance().removeShortcut();
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();

                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (CrewCloudApplication.isActivityVisible()) {
            if (CrewCloudApplication.getInstance().getMainActivityInstance() != null) {
                CrewCloudApplication.getInstance().getMainActivityInstance().refreshMainURL();
            }
        } else {
            CrewCloudApplication.getInstance().setHasUpdate(true);
        }

        if (mapData.containsKey("message")) {
            ShowNotification(mapData.get("message"),
                    mapData.get("title"),
                    mapData.get("writer"),
                    mapData.get("url"), badgeCount);
        }
    }

    private void ShowNotification(String message, String title, String writer, String url, int count) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setNumber(count)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(count == 0) {
            notificationManager.cancelAll();
            return;
        }

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notifyID /* ID of notification */, notificationBuilder.build());
    }
}