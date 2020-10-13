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

    private String channelId = "0011001";
    private String channelName = "CrewApproval 0011001";
    private String channelIdNonSound = "0022002";
    private String channelNameNonSound = "CrewApproval 0022002";
    private NotificationChannel channel1, channel2;
    boolean isEnableSound = true, isEnableVibrate = true;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> mapData = remoteMessage.getData();

        PreferenceUtilities prefs = CrewCloudApplication.getInstance().getPreferenceUtilities();
        isEnableVibrate = prefs.getNOTIFI_VIBRATE();
        isEnableSound = prefs.getNOTIFI_SOUND();

        int badgeCount = 0;
        try {
            String value = mapData.get("badgecount");

            if (value != null) {
                badgeCount = Integer.parseInt(value);
                if (badgeCount > 0) {
                    CrewCloudApplication.getInstance().shortcut(badgeCount);
                } else {
                    CrewCloudApplication.getInstance().removeShortcut();
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

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private void ShowNotification(String message, String title, String writer, String url, int count) {
        PreferenceUtilities prefs = CrewCloudApplication.getInstance().getPreferenceUtilities();
        boolean isNewMail = prefs.getNOTIFI_MAIL();
        boolean isTime = prefs.getNOTIFI_TIME();
        String strFromTime = prefs.getSTART_TIME();
        String strToTime = prefs.getEND_TIME();

        final long[] vibrate = new long[]{1000, 1000, 1000, 1000, 1000};
        final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.URL_ALARM, url);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        String idChanel = isEnableSound ? channelId : channelIdNonSound;
        mBuilder = new NotificationCompat.Builder(this, idChanel)
                .setSmallIcon(R.drawable.notification_approval)
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.approval))
                .setContentText(title)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setTicker(message)
                .setNumber(count)
                .setContentTitle(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = isEnableSound ? channel1 : channel2;
            mChannel.setShowBadge(true);

            if (isEnableVibrate) {
                mBuilder.setVibrate(vibrate);
                Vibrator v = (Vibrator) CrewCloudApplication.getInstance().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            } else {
                final long[] noVibrate = new long[]{0, 0, 0, 0, 0};
                mBuilder.setVibrate(noVibrate);
                Vibrator v = (Vibrator) CrewCloudApplication.getInstance().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(0);
            }
        } else {
            // Check notification setting and config notification
            if (isEnableSound) {
                mBuilder.setSound(soundUri);
            } else {
                mBuilder.setSound(null);
            }

            if (isEnableVibrate) {
                mBuilder.setVibrate(vibrate);
                Vibrator v = (Vibrator) CrewCloudApplication.getInstance().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            } else {
                final long[] noVibrate = new long[]{0, 0, 0, 0, 0};
                mBuilder.setVibrate(noVibrate);
                Vibrator v = (Vibrator) CrewCloudApplication.getInstance().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(0);
            }
        }

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(Html.fromHtml("<font color='#878787'>" + title + "</font>" + "<br/>" + writer));
        mBuilder.setStyle(bigTextStyle);


        if (isNewMail) {
            if (isTime) {
                if (TimeUtils.isBetweenTime(strFromTime, strToTime)) {
                    mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
                }
            } else {
                mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel1 = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel2 = new NotificationChannel(channelIdNonSound, channelNameNonSound, NotificationManager.IMPORTANCE_LOW);
        }
        startForeground(1, getNotification());
    }

    public Notification getNotification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel).setSmallIcon(android.R.drawable.ic_menu_mylocation).setContentTitle("CrewApproval");
        Notification notification = mBuilder
                .setPriority(PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();


        return notification;
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = null;
        String chanelId = isEnableSound ? channelId : channelIdNonSound;
        if (isEnableSound) {
            mChannel = channel1;
        } else {
            mChannel = channel2;
        }

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }

        return chanelId;
    }
}