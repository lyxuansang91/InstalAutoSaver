package com.adu.instaautosaver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.adu.instaautosaver.acitivity.SettingActivity;
import com.adu.instaautosaver.constant.Constants;
import com.adu.instaautosaver.constant.ExtraBundleKeyConstants;
import com.adu.instaautosaver.download.DownloadManager;
import com.adu.instaautosaver.utils.NotificationHelper;

/**
 * Created by Thomc on 15/02/2016.
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (Constants.NOTIFICATION_CONFIRM_DOWNLOAD_YES.equals(action)) {
            Log.v(TAG, "Pressed Yes");
            if (bundle != null) {
                DownloadManager.getInstance(context).download(bundle.getString(ExtraBundleKeyConstants.EXTRA_NOTIFICATION_URL));
            }
        } else if (Constants.NOTIFICATION_CONFIRM_DOWNLOAD_CLOSE.equals(action)) {
            Log.v(TAG, "Pressed Close");
        } else if (Constants.NOTIFICATION_CONFIRM_DOWNLOAD_SETTING.equals(action)) {
            Log.v(TAG, "Pressed Setting");
            Intent intentSetting = new Intent(context, SettingActivity.class);
            intentSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentSetting);
        }
        NotificationHelper.closeConfirmNotification(context);

    }
}
