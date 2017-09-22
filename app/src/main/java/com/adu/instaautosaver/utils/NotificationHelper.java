package com.adu.instaautosaver.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.acitivity.MainActivity;
import com.adu.instaautosaver.acitivity.PhotoViewActivity;
import com.adu.instaautosaver.acitivity.VideoViewActivity;
import com.adu.instaautosaver.constant.Constants;
import com.adu.instaautosaver.constant.ExtraBundleKeyConstants;
import com.adu.instaautosaver.entity.InstagMedia;

import java.util.Random;

public class NotificationHelper {
    public static final int NOTIFICATION_CONFIRM_DOWNLOAD_ID = 1760;
    public static final int NOTIFICATION_DOWNLOADING_ID = 10101;
    private static final int REQUEST_CODE = 1000;

    public static void showConfirmDownloadNotification(Context context, String url) {
        CharSequence tickerText = context.getString(R.string.app_name);
        long when = System.currentTimeMillis();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.build();
        notification.when = when;
        notification.tickerText = tickerText;
        notification.icon = R.drawable.ic_launcher;

        RemoteViews contentView = null;
        //set content
        if (Build.VERSION.SDK_INT < 16) {
            contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout_api_below_16);
            notification.contentView = contentView;
        } else {
            contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
            notification.bigContentView = contentView;
        }
        Bundle bundle = new Bundle();
        bundle.putString(ExtraBundleKeyConstants.EXTRA_NOTIFICATION_URL, url);
        bundle.putInt(ExtraBundleKeyConstants.EXTRA_NOTIFICATION_ID, NOTIFICATION_CONFIRM_DOWNLOAD_ID);
        //content intent
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtras(bundle);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, REQUEST_CODE,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = pendingNotificationIntent;

        //Yes intent
        Intent yesReceive = new Intent();
        yesReceive.setAction(Constants.NOTIFICATION_CONFIRM_DOWNLOAD_YES);
        yesReceive.putExtras(bundle);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(context, REQUEST_CODE, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.imgDownload, pendingIntentYes);
        //Setting intent
        Intent settingReceive = new Intent();
        settingReceive.setAction(Constants.NOTIFICATION_CONFIRM_DOWNLOAD_SETTING);
        PendingIntent pendingIntentSetting = PendingIntent.getBroadcast(context, REQUEST_CODE, settingReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.imgSetting, pendingIntentSetting);
        //Close intent
        Intent noReceive = new Intent();
        noReceive.setAction(Constants.NOTIFICATION_CONFIRM_DOWNLOAD_CLOSE);
        PendingIntent pendingIntentNo = PendingIntent.getBroadcast(context, REQUEST_CODE, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.imgClose, pendingIntentNo);

        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_CONFIRM_DOWNLOAD_ID, notification);
    }

    public static void closeConfirmNotification(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_CONFIRM_DOWNLOAD_ID);
    }

    public static void closeNotification(Context context, NotificationCompat.Builder notification) {
        Bundle bundle = notification.getExtras();
        int id = bundle.getInt(ExtraBundleKeyConstants.EXTRA_NOTIFICATION_ID);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }

    public static NotificationCompat.Builder buildDownloadingNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_launcher);
        builder.setProgress(0, 0, true);
        int id = new Random().nextInt();
        Bundle bundle = new Bundle();
        bundle.putInt(ExtraBundleKeyConstants.EXTRA_NOTIFICATION_ID, id);
        builder.setExtras(bundle);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
        return builder;
    }

    public static void updateDownloadingNotification(Context context, NotificationCompat.Builder builder, int progress, InstagMedia media) {
        Bundle bundle = builder.getExtras();
        int id = bundle.getInt(ExtraBundleKeyConstants.EXTRA_NOTIFICATION_ID);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (progress == 100) {
            builder.setProgress(100, progress, false);
            builder.setContentText("Download complete")
                    .setProgress(0, 0, false);
            //content intent
            Intent notificationIntent = new Intent(context, PhotoViewActivity.class);
            if (!media.isIsPhoto()) {
                notificationIntent = new Intent(context, VideoViewActivity.class);
            }
            bundle.putString(ExtraBundleKeyConstants.EXTRA_MEDIA_URL, media.getLocalFileUrl());
            notificationIntent.putExtras(bundle);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, REQUEST_CODE,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = builder.build();
            notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
            notification.contentIntent = pendingNotificationIntent;
            notificationManager.notify(id, notification);
        } else {
            builder.setProgress(100, progress, false);
            notificationManager.notify(id, builder.build());
        }

    }
}
