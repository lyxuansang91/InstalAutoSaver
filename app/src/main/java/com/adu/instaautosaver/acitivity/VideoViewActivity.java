package com.adu.instaautosaver.acitivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.constant.ExtraBundleKeyConstants;
import com.adu.instaautosaver.utils.AppConfigHelper;

/**
 * Created by Thomc on 20/02/2016.
 */
public class VideoViewActivity extends BaseActivity {
    private ProgressDialog mDialog;
    private VideoView mVideoView;

    //    String videoUrl = "/sdcard/InstaAutoSaver/chitho87__295402847__BABfaxIrIKR.mp4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        initViews();
        String videoUrl = null;
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                videoUrl = bundle.getString(ExtraBundleKeyConstants.EXTRA_MEDIA_URL);
            }
        }
        if (videoUrl == null) {
            finish();
        } else {
            playVideo(videoUrl);
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
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mDialog = new ProgressDialog(VideoViewActivity.this);
    }

    private void playVideo(String url) {
        mDialog.setMessage("Buffering...");
        mDialog.setIndeterminate(false);
        mDialog.setCancelable(true);
        mDialog.show();
        try {
            MediaController mediacontroller = new MediaController(
                    VideoViewActivity.this);
            mediacontroller.setAnchorView(mVideoView);
//            Uri video = Uri.parse(url);
            mVideoView.setMediaController(mediacontroller);
            mVideoView.setVideoPath(url);
            mVideoView.requestFocus();
            mVideoView.start();
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mDialog.dismiss();
                    mVideoView.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mDialog.dismiss();
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}