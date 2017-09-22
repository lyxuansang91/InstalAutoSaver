package com.adu.instaautosaver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adu.instaautosaver.constant.Constants;
import com.adu.instaautosaver.utils.AlarmServiceHelper;
import com.adu.instaautosaver.utils.TinyDB;

/**
 * Created by Thomc on 15/02/2016.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TinyDB.getInstance(context).getBoolean(Constants.PREF_AUTO_DOWNLOAD, true)) {
            AlarmServiceHelper.getInstance(context).start();
        }
    }
}
