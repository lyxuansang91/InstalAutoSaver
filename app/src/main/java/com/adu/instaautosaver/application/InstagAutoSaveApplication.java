package com.adu.instaautosaver.application;

import android.app.Application;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.utils.ImageLoaderHelper;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class InstagAutoSaveApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initFont();
        ImageLoaderHelper.initImageLoader(this);
    }

    private void initFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("font/Roboto-Medium.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}
