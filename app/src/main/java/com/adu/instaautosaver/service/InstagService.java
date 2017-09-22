package com.adu.instaautosaver.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.adu.instaautosaver.constant.Constants;
import com.adu.instaautosaver.utils.NotificationHelper;
import com.adu.instaautosaver.utils.TinyDB;

import java.util.Timer;
import java.util.TimerTask;

public class InstagService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {

    private static final String TAG = "INSTAG_SERVICE";

    /**
     * interface for clients that bind
     */
    IBinder mBinder;

    /**
     * indicates whether onRebind should be used
     */
    boolean mAllowRebind;
    private Timer mTimer;

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {
        Log.d(TAG, "on service create");
    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "on service start command");
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(this);
        return START_STICKY;
    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {

    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on service destroy");
        sendBroadcast(new Intent(Constants.INSTAG_SERVICE_DESTROYED));
        cancelTimer();
    }


    public void startTimer() {
        cancelTimer();
        final Handler mHandler = new Handler();
        mTimer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onTick();
                    }
                });
            }
        };
        mTimer.schedule(timerTask, 0, 30 * 60 * 1000);

    }


    private void onTick() {
        Log.d(TAG, "timer ontick...");
    }

    public void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onPrimaryClipChanged() {
        if (TinyDB.getInstance(this).getBoolean(Constants.PREF_AUTO_DOWNLOAD, true)) {
            ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            CharSequence pasteData = "";
            ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText();
            if (pasteData != null) {
                String input = pasteData.toString().trim();
                if (input.startsWith(Constants.INSTAG_HTTPS_LINK_PREFIX) || input.startsWith(Constants.INSTAG_HTTP_LINK_PREFIX)) {
                    NotificationHelper.showConfirmDownloadNotification(InstagService.this, pasteData.toString());
                }
            }
        }
    }
}
