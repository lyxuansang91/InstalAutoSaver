package com.adu.instaautosaver.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ContentLengthInputStream;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.L;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author Thomc May 18, 2015
 */
public class ImageLoaderHelper {
    public static void initImageLoader(Context context) {
        L.disableLogging();
        try {
//            AuthImageDownloader downloader = new ImageLoaderHelper().new AuthImageDownloader(
//                    context);
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    context).threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
//                    .imageDownloader(downloader)
                    .build();
            ImageLoader.getInstance().init(config);

        } catch (Exception e) {

        }
    }

    public static final DisplayImageOptions DEFAULT_OPTIONS = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
            .cacheOnDisc(true)
            .resetViewBeforeLoading(true)
//            .showImageOnFail(R.drawable.chat_avatar_def)
//            .showImageOnLoading(R.drawable.chat_avatar_def)
//            .showImageForEmptyUri(R.drawable.chat_avatar_def)
            .displayer(new RoundedBitmapDisplayer(0)).build();
    public static final DisplayImageOptions PROFILE_AVATAR_OPTIONS = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(false)
//            .showImageOnFail(R.drawable.chat_avatar_def)
//            .showImageOnLoading(R.drawable.chat_avatar_def)
//            .showImageForEmptyUri(R.drawable.chat_avatar_def)
            .cacheOnDisc(false).considerExifParams(true)
            .resetViewBeforeLoading(true)
                    // .showImageOnFail(R.drawable.chat_avatar_def)
                    // .showImageOnLoading(R.drawable.chat_avatar_def)
            .displayer(new RoundedBitmapDisplayer(0)).build();

    private static final List<String> displayedImages = Collections
            .synchronizedList(new LinkedList<String>());
    public static AnimateFirstDisplayListener DEFAULT_LISTENER = new AnimateFirstDisplayListener();

    public static class AnimateFirstDisplayListener extends

            SimpleImageLoadingListener {

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            // Log.v("", "onLoadingComplete: " + (loadedImage == null));
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                if (imageView != null) {
                    imageView.setImageBitmap(loadedImage);
                    FadeInBitmapDisplayer.animate(imageView, 000);
                }
            }
        }

    }

    public static final String TAG = AuthImageDownloader.class.getName();

    public class AuthImageDownloader extends BaseImageDownloader {

        public AuthImageDownloader(Context context) {
            super(context);
        }

        @Override
        protected InputStream getStreamFromNetwork(String imageUri, Object extra)
                throws IOException {
            if (Scheme.ofUri(imageUri) == Scheme.HTTPS) {
                HttpURLConnection conn = createHttpsConnection(imageUri, extra);

                int redirectCount = 0;
                while (conn.getResponseCode() / 100 == 3
                        && redirectCount < MAX_REDIRECT_COUNT) {
                    conn = createConnection(conn.getHeaderField("Location"),
                            extra);
                    redirectCount++;
                }

                InputStream imageStream;
                try {
                    imageStream = conn.getInputStream();
                } catch (IOException e) {
                    // Read all data to allow reuse connection
                    // (http://bit.ly/1ad35PY)
                    IoUtils.readAndCloseStream(conn.getErrorStream());
                    throw e;
                }
                return new ContentLengthInputStream(new BufferedInputStream(
                        imageStream, BUFFER_SIZE), conn.getContentLength());
            } else {
                return super.getStreamFromNetwork(imageUri, extra);
            }

        }
    }

    protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
    /**
     * {@value}
     */
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds
    protected static final int MAX_REDIRECT_COUNT = 5;

    protected static HttpURLConnection createHttpsConnection(String url,
                                                             Object extra) throws IOException {
        String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
        trustAllHosts();
        HttpsURLConnection conn = (HttpsURLConnection) new URL(encodedUrl)
                .openConnection();
        conn.setHostnameVerifier(DO_NOT_VERIFY);
        conn.setConnectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT);
        conn.setReadTimeout(DEFAULT_HTTP_READ_TIMEOUT);
        return conn;
    }

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // always verify the host - dont check for certificate

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
