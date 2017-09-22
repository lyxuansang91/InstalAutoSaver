package com.adu.instaautosaver.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adu.instaautosaver.constant.Constants;
import com.adu.instaautosaver.receiver.AlarmReceiver;
import com.adu.instaautosaver.service.InstagService;

/**
 * Created by Thomc on 17/02/2016.
 */
public class AlarmServiceHelper {
    private static final String TAG = "INSTAG_SERVICE";
    private Context mContext;
    private static AlarmServiceHelper sInstance;

    private AlarmServiceHelper(Context context) {
        this.mContext = context;
    }

    public static AlarmServiceHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AlarmServiceHelper(context);
        }
        return sInstance;
    }

    public void start() {
        if (TinyDB.getInstance(mContext).getBoolean(Constants.PREF_AUTO_DOWNLOAD, true)) {
            Log.v(TAG, "Start alarm service");
            mContext.startService(new Intent(mContext, InstagService.class));
            Intent intent = new Intent(mContext, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
            AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + Constants.ALARM_SERVICE_SCHEDULE_TIME, Constants.ALARM_SERVICE_SCHEDULE_TIME, pendingIntent);
        }
    }

    public void stop() {
        Log.v(TAG, "stop alarm service");
        mContext.stopService(new Intent(mContext, InstagService.class));
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }
}
