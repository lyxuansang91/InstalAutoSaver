package com.adu.instaautosaver.download;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.adu.instaautosaver.db.DatabaseHelper;
import com.adu.instaautosaver.entity.InstagMedia;
import com.adu.instaautosaver.entity.InstagUser;
import com.adu.instaautosaver.utils.FileHelper;
import com.adu.instaautosaver.utils.InstagUrlParser;
import com.adu.instaautosaver.utils.NotificationHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by Thomc on 17/02/2016.
 */
public class InstagDownload {
    private static final String TAG = "InstagDownload";
    private NotificationCompat.Builder mNotification;
    private InstagMedia mMedia;
    private DownloadManager mManager;
    private boolean mIsCompleted;
    private String mUrl;
    private Context mContext;

    public InstagDownload(Context context, DownloadManager manager, String url) {
        this.mContext = context;
        this.mUrl = url;
        this.mManager = manager;
        prepare();
    }

    private void prepare() {
        mNotification = NotificationHelper.buildDownloadingNotification(getContext());
    }

    public void download() {
        InstagUrlParser.parseUrl(mUrl, new InstagUrlParser.InstagUrlParserCallback() {
            @Override
            public void onResults(InstagMedia result) {
                mMedia = result;
                if (mMedia == null) {
                    onDownloadFailure();
                } else {
                    try {
                        InstagUser user = result.getOwner();
                        DatabaseHelper.getInstance(mContext).updatePageData(user);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    downloadAndSave();
                }

            }
        });
    }

    private void downloadAndSave() {
        new AsyncTask<Void, Integer, Object>() {
            final long MAX_ELAPSED_TIME_UPDATE_PROGRESS = 500;
            private long mLastTimeUpdated;

            @Override
            protected Object doInBackground(Void... params) {
                boolean res = downloadAndSave(mMedia.getDownloadUrl(), mMedia.buildLocalFileName(), new DownloadProgressCallback() {
                    @Override
                    public void onDownloadProgressing(int progress) {
                        publishProgress(progress);
                    }
                });
                if (res) {
                    return new Object();
                } else {
                    return null;
                }

            }

            @Override
            protected void onProgressUpdate(final Integer... values) {
                long elapsedTime = System.currentTimeMillis() - mLastTimeUpdated;
                if (elapsedTime >= MAX_ELAPSED_TIME_UPDATE_PROGRESS) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            NotificationHelper.updateDownloadingNotification(getContext(), mNotification, values[0], mMedia);
                            mLastTimeUpdated = System.currentTimeMillis();
                        }
                    });
                }


            }

            @Override
            protected void onPostExecute(Object result) {
                Log.d(TAG, "onPostExecute");
                if (result != null) {
                    complete();
                    mMedia.setLocalFileUrl(FileHelper.FILE_PREFIX + FileHelper.getAppFolder().getAbsolutePath() + "/" + mMedia.buildLocalFileName());
                    NotificationHelper.updateDownloadingNotification(getContext(), mNotification, 100, mMedia);
                } else {
                    onDownloadFailure();
                }
            }
        }.execute();


    }

    public static boolean downloadAndSave(String url, String fileName, DownloadProgressCallback callback) {
        try {
            URL mediaUrl = new URL(url);
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
            HttpConnectionParams.setSoTimeout(httpParams, 10000);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpGet httpget = new HttpGet(mediaUrl.toURI());
            HttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            int fileSize = (int) entity.getContentLength();
            if (fileSize <= 0) {
                return false;
            }
            InputStream inputStream = new BufferedInputStream(entity.getContent(), 8192);
            File file = new File(FileHelper.getAppFolder(), fileName);
            OutputStream outputStream = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int dataSize;
            int loadedSize = 0;
            while ((dataSize = inputStream.read(buffer)) != -1) {
                loadedSize += dataSize;
                if (callback != null)
                    callback.onDownloadProgressing(((loadedSize * 100) / fileSize));
                outputStream.write(buffer, 0, dataSize);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void onDownloadFailure() {
        Log.d(TAG, "onDownloadFailure");
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Cannot download this photo!", Toast.LENGTH_SHORT).show();
                NotificationHelper.closeNotification(getContext(), mNotification);
                complete();
            }
        });

    }

    private void complete() {
        Log.d(TAG, "completed");
        mIsCompleted = true;
        mManager.onCompleted(InstagDownload.this);
    }


    public NotificationCompat.Builder getNotification() {
        return mNotification;
    }

    public void setNotification(NotificationCompat.Builder mNotification) {
        this.mNotification = mNotification;
    }

    public InstagMedia getMedia() {
        return mMedia;
    }

    public void setMedia(InstagMedia mMedia) {
        this.mMedia = mMedia;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.mIsCompleted = isCompleted;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public Context getContext() {
        return mContext;
    }
}
