package com.adu.instaautosaver.download;

import android.content.Context;

import java.util.Stack;

/**
 * Created by Thomc on 17/02/2016.
 */
public class DownloadManager {
    private static DownloadManager sInstance;
    private Stack<InstagDownload> mWaitingList;
    private InstagDownload mCurrentDownload;
    private Context mContext;

    public static DownloadManager getInstance(Context context) {
        if (sInstance == null)
            sInstance = new DownloadManager(context);
        return sInstance;
    }

    private DownloadManager(Context context) {
        this.mContext = context;
        mWaitingList = new Stack<>();
    }

    public void download(String url) {
        InstagDownload instagDownload = new InstagDownload(mContext, this, url);
        if (mCurrentDownload == null || (mCurrentDownload != null && mCurrentDownload.isCompleted())) {
            mCurrentDownload = instagDownload;
            mCurrentDownload.download();
        } else {
            mWaitingList.push(instagDownload);
        }
    }

    public void onCompleted(InstagDownload download) {
        if (mWaitingList != null && !mWaitingList.isEmpty()) {
            mCurrentDownload = mWaitingList.peek();
            mCurrentDownload.download();
            mWaitingList.pop();
        }
    }

    public Context getContext() {
        return mContext;
    }
}
