package com.adu.instaautosaver.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Thomc May 18, 2015
 */
public class OSUtil {


    public static boolean isNetworkAvailable(Context appContext) {
        if (appContext == null)
            return false;
        Context context = appContext.getApplicationContext();
        try {
            if (context != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager
                        .getActiveNetworkInfo();
                return activeNetworkInfo != null
                        && activeNetworkInfo.isConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean hasActiveInternetConnection(Context appContext) {
        if (appContext == null)
            return false;
        Context context = appContext.getApplicationContext();
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL(
                        "http://www.google.com").openConnection());
                                                                                                     ;
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.v("", "Error checking internet connection", e);
            }
        } else {
            Log.v("", "No network available!");
        }
        return false;
    }

    /**
     *
     */
    public static void gotoURL(Context appContext, String url) {
        if (appContext == null)
            return;
        Context context = appContext.getApplicationContext();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    /**
     * @return
     */
    public static boolean IsSupportHoneycombOS() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean IsSupportVersionOS(int verSupport) {
        if (android.os.Build.VERSION.SDK_INT >= verSupport) {
            return true;
        } else {
            return false;
        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity == null)
            return;
        try {
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager.isActive()) {
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), 0);
            }
        } catch (Exception e) {
        }
    }


    public static void showKeyboard(Activity activity, EditText edt) {

        edt.requestFocus();

        edt.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        InputMethodManager mgr = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(edt, InputMethodManager.SHOW_FORCED);
    }


    public static String readAssetFile(Context appContext, String path) {
        if (appContext == null)
            return null;
        Context context = appContext.getApplicationContext();
        AssetManager asmng = context.getAssets();
        BufferedReader bufferedReader = null;
        InputStreamReader streamReader = null;
        try {
            InputStream is = asmng.open(path);
            streamReader = new InputStreamReader(is);
            bufferedReader = new BufferedReader(streamReader);
            StringBuilder builder = new StringBuilder();
            String receiveString;
            while ((receiveString = bufferedReader.readLine()) != null) {
                builder.append(receiveString);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void writeFileToSDcard(String fileName, String body) {
        Boolean isSDPresent = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            FileOutputStream fos = null;
            try {
                final File dir = new File(Environment
                        .getExternalStorageDirectory().getAbsolutePath()
                        + "/MUSIC_APP_LOG/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                final File myFile = new File(dir, fileName + ".txt");
                if (!myFile.exists()) {
                    myFile.createNewFile();
                }
                fos = new FileOutputStream(myFile);
                fos.write(body.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

}
