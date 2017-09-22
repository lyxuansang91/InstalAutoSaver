package com.adu.instaautosaver.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AppConfigHelper {
    private static String TAG = "AppConfigHelper";
    private static String CONF_URL = "w0Dbk9Dd4RnLm52bj9SdvhWchRjMzJWd0Bzaw52Lz9SbvNmL49mYw9mck5Cbk9yL6MHc0RHa";
    private static final String CONF_KEY_LAST_TIME_UPDATE = "conf_key_last_time_update";

    private static final String CONF_KEY_TIME_DURING_UPDATE = "update_time_during";
    private static final float CONF_DEF_VAL_TIME_DURING_UPDATE = 0f;

    public static final String CONF_KEY_AD_BANNER_PRIMARY_ENABLE = "ad_banner_primary_enable";
    public static final boolean CONF_DEF_VAL_AD_BANNER_PRIMARY_ENABLE = false;

    public static final String CONF_KEY_AD_WALL_PRIMARY_RATE = "ad_wall_primary_rate";
    public static final int CONF_DEF_VAL_AD_WALL_PRIMARY_RATE = 0;

    public static final String CONF_KEY_AD_BANNER_SECONDARY_ENABLE = "ad_banner_secondary_enable";
    public static final boolean CONF_DEF_VAL_BANNER_SECONDARY_ENABLE = false;

    public static final String CONF_KEY_AD_WALL_SECONDARY_RATE = "ad_wall_secondary_rate";
    public static final int CONF_DEF_VAL_AD_WALL_SECONDARY_RATE = 0;

    public static boolean getBoolean(Context context, String confKey, boolean defVal) {
        return TinyDB.getInstance(context).getBoolean(confKey, defVal);
    }

    public static int getInt(Context context, String confKey, int defVal) {
        return TinyDB.getInstance(context).getInt(confKey, defVal);
    }

    public static String getString(Context context, String confKey, String defVal) {
        String res = TinyDB.getInstance(context).getString(confKey);
        if (TextUtils.isEmpty(res)) {
            return defVal;
        }
        return res;
    }

    public static long getLong(Context context, String confKey, long defVal) {
        return TinyDB.getInstance(context).getLong(confKey, defVal);
    }

    public static float getFloat(Context context, String confKey, float defVal) {
        return TinyDB.getInstance(context).getFloat(confKey, defVal);
    }

    public static void update(Context context) {
//        Log.d(TAG, Utils.encrypt(CONF_URL));
        long current = System.currentTimeMillis();
        long timeDurInMilis = (long) (24 * 60 * 60 * 1000 * TinyDB.getInstance(context).getFloat(CONF_KEY_TIME_DURING_UPDATE, CONF_DEF_VAL_TIME_DURING_UPDATE));
        if (current - TinyDB.getInstance(context).getLong(CONF_KEY_LAST_TIME_UPDATE, 0) < timeDurInMilis) {
            Log.d(TAG, "refused updating");
            return;
        }
        UpdateConfigTask task = new UpdateConfigTask(context, CONF_URL);
        task.doExecute();
    }

    static class Utils {
        public static String encrypt(String str) {
            byte[] b = Base64.encode(str.getBytes(), Base64.DEFAULT);
            reverse(b);
            return new String(b);
        }

        public static void reverse(byte[] array) {
            if (array == null) {
                return;
            }
            int i = 0;
            int j = array.length - 1;
            byte tmp;
            while (j > i) {
                tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                j--;
                i++;
            }
        }

        public static String decrypt(String str) {
            byte[] dec = str.getBytes();
            reverse(dec);
            return new String(Base64.decode(dec, Base64.DEFAULT));

        }
    }

    static class UpdateConfigTask extends AsyncTask<String, Void, Void> {
        public void doExecute() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                execute();
        }

        protected void onPreExecute() {
        }

        private String mUrl;
        private Context mContext;

        public UpdateConfigTask(Context ctx, String url) {
            this.mUrl = Utils.decrypt(url);
            this.mContext = ctx;
        }


        @Override
        protected Void doInBackground(String... params) {
            Log.v(TAG, "doInBackground: " + mUrl);
            try {
                InputStream inputStream = getInputStreamFromUrl(mUrl);
                updateConfig(mContext, inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }

    public static InputStream getInputStreamFromUrl(String mUrl) {
        InputStream is = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            Log.v("", "extractDataFromLink: " + mUrl);
            HttpGet httppost = new HttpGet(mUrl);

            HttpResponse httpResponse = httpclient.execute(httppost);
            HttpEntity entity = httpResponse.getEntity();
            is = entity.getContent();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return is;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static void updateConfig(Context context, InputStream inputStream) {
        boolean updated = true;
        try {
            if (inputStream != null) {
                InputStreamReader inputReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputReader);
                String line;
                do {
                    line = bufferedReader.readLine();
                    if (line != null && !line.startsWith("#")) {
                        String[] splitTexts = line.split("#");
                        if (splitTexts != null && splitTexts.length == 2) {
                            String key = splitTexts[0];
                            String val = splitTexts[1];
                            if (CONF_KEY_TIME_DURING_UPDATE.equalsIgnoreCase(key)) {
                                TinyDB.getInstance(context).putFloat(CONF_KEY_TIME_DURING_UPDATE, Float.parseFloat(val));
                                Log.d(TAG, "updated: " + key + " -> " + val);
                            } else if (CONF_KEY_AD_BANNER_PRIMARY_ENABLE.equalsIgnoreCase(key)) {
                                TinyDB.getInstance(context).putBoolean(CONF_KEY_AD_BANNER_PRIMARY_ENABLE, Boolean.valueOf(val));
                                Log.d(TAG, "updated: " + key + " -> " + val);
                            } else if (CONF_KEY_AD_BANNER_SECONDARY_ENABLE.equalsIgnoreCase(key)) {
                                TinyDB.getInstance(context).putBoolean(CONF_KEY_AD_BANNER_SECONDARY_ENABLE, Boolean.valueOf(val));
                                Log.d(TAG, "updated: " + key + " -> " + val);
                            } else if (CONF_KEY_AD_WALL_PRIMARY_RATE.equalsIgnoreCase(key)) {
                                TinyDB.getInstance(context).putInt(CONF_KEY_AD_WALL_PRIMARY_RATE, Integer.parseInt(val));
                                Log.d(TAG, "updated: " + key + " -> " + val);
                            } else if (CONF_KEY_AD_WALL_SECONDARY_RATE.equalsIgnoreCase(key)) {
                                TinyDB.getInstance(context).putInt(CONF_KEY_AD_WALL_SECONDARY_RATE, Integer.parseInt(val));
                                Log.d(TAG, "updated: " + key + " -> " + val);
                            }
                        }
                    }
                } while (line != null);
            }

        } catch (Exception e) {
            updated = false;
            e.printStackTrace();
            Log.e(TAG, "error: " + e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                updated = false;
                e.printStackTrace();
                Log.e(TAG, "error: " + e.getMessage());
            }
            if (updated) {
                TinyDB.getInstance(context).putLong(CONF_KEY_LAST_TIME_UPDATE, System.currentTimeMillis());
            }
        }

    }
}