package com.adu.instaautosaver.acitivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.constant.ExtraBundleKeyConstants;
import com.adu.instaautosaver.utils.AppConfigHelper;
import com.adu.instaautosaver.utils.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.polites.android.GestureImageView;

/**
 * Created by Thomc on 20/02/2016.
 */
public class PhotoViewActivity extends BaseActivity {
    private GestureImageView mPhotoView;
    private String photoUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                photoUrl = bundle.getString(ExtraBundleKeyConstants.EXTRA_MEDIA_URL);
            }
        }
        if (photoUrl == null) {
            finish();
        } else {
            initViews();
        }
        showAdWall(AppConfigHelper.getInt(this, AppConfigHelper.CONF_KEY_AD_WALL_SECONDARY_RATE, AppConfigHelper.CONF_DEF_VAL_AD_WALL_SECONDARY_RATE));
    }

    protected void onResume() {
        super.onResume();
        if (AppConfigHelper.getBoolean(this, AppConfigHelper.CONF_KEY_AD_BANNER_SECONDARY_ENABLE, AppConfigHelper.CONF_DEF_VAL_BANNER_SECONDARY_ENABLE)) {
            loadAd();
        }
    }

    protected void initViews() {
        super.initViews();
        mPhotoView = (GestureImageView) findViewById(R.id.gestureImage);
        ImageLoader.getInstance().displayImage(photoUrl, mPhotoView, ImageLoaderHelper.DEFAULT_LISTENER);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}