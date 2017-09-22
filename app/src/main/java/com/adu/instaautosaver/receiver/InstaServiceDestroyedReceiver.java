package com.adu.instaautosaver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adu.instaautosaver.constant.Constants;
import com.adu.instaautosaver.service.InstagService;
import com.adu.instaautosaver.utils.TinyDB;

/**
 * Created by Thomc on 15/02/2016.
 */
public class InstaServiceDestroyedReceiver extends BroadcastReceiver {
    private static final String TAG = "INSTAG_SERVICE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "on service destroy receive");
        if (TinyDB.getInstance(context).getBoolean(Constants.PREF_AUTO_DOWNLOAD, true)) {
            context.startService(new Intent(context.getApplicationContext(), InstagService.class));
        }
    }
}
