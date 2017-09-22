package com.adu.instaautosaver.acitivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.constant.Constants;
import com.adu.instaautosaver.utils.AppConfigHelper;
import com.adu.instaautosaver.utils.AppUtils;
import com.adu.instaautosaver.utils.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Thomc on 25/02/2016.
 */
public class HelpActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imgGuide01;
    private ImageView imgGuide02;
    private ImageView imgGuide03;
    private ImageView imgGuide04;
    private TextView txtWatchVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initViews();
    }

    protected void onResume() {
        super.onResume();
        if (AppConfigHelper.getBoolean(this, AppConfigHelper.CONF_KEY_AD_BANNER_SECONDARY_ENABLE, AppConfigHelper.CONF_DEF_VAL_BANNER_SECONDARY_ENABLE)) {
            loadAd();
        }
    }

    protected void initViews() {
        super.initViews();
        imgGuide01 = (ImageView) findViewById(R.id.imgGuide01);
        imgGuide02 = (ImageView) findViewById(R.id.imgGuide02);
        imgGuide03 = (ImageView) findViewById(R.id.imgGuide03);
        imgGuide04 = (ImageView) findViewById(R.id.imgGuide04);
        ImageLoader.getInstance().displayImage("assets://img/img_guide_01.jpg", imgGuide01, ImageLoaderHelper.DEFAULT_OPTIONS, ImageLoaderHelper.DEFAULT_LISTENER);
        ImageLoader.getInstance().displayImage("assets://img/img_guide_02.jpg", imgGuide02, ImageLoaderHelper.DEFAULT_OPTIONS, ImageLoaderHelper.DEFAULT_LISTENER);
        ImageLoader.getInstance().displayImage("assets://img/img_guide_03.jpg", imgGuide03, ImageLoaderHelper.DEFAULT_OPTIONS, ImageLoaderHelper.DEFAULT_LISTENER);
        ImageLoader.getInstance().displayImage("assets://img/img_guide_04.jpg", imgGuide04, ImageLoaderHelper.DEFAULT_OPTIONS, ImageLoaderHelper.DEFAULT_LISTENER);
        txtWatchVideo = (TextView) findViewById(R.id.txtWatchVideo);
        txtWatchVideo.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v == txtWatchVideo) {
            AppUtils.watchYoutubeVideo(this, Constants.YOUTUBE_VIDEO_ID);
        }
    }

}
