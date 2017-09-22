package com.adu.instaautosaver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adu.instaautosaver.constant.Constants;
import com.adu.instaautosaver.service.InstagService;
import com.adu.instaautosaver.utils.TinyDB;

/**
 * Created by Thomc on 17/02/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "INSTAG_SERVICE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "on alarm receive");
        if (TinyDB.getInstance(context).getBoolean(Constants.PREF_AUTO_DOWNLOAD, true)) {
            Intent newService = new Intent(context, InstagService.class);
            context.startService(newService);
        }
    }
}