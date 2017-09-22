package com.adu.instaautosaver.acitivity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.constant.Constants;
import com.adu.instaautosaver.utils.AlarmServiceHelper;
import com.adu.instaautosaver.utils.AppConfigHelper;
import com.adu.instaautosaver.utils.AppUtils;
import com.adu.instaautosaver.utils.FileHelper;
import com.adu.instaautosaver.utils.TinyDB;

/**
 * Created by Thomc on 25/02/2016.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private SwitchCompat mSwitchAutoDownload;
    private TextView txtAppFolder;
    private ViewGroup btnOpenAppFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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
        mSwitchAutoDownload = (SwitchCompat) findViewById(R.id.switchAutoDownload);
        mSwitchAutoDownload.setChecked(TinyDB.getInstance(this).getBoolean(Constants.PREF_AUTO_DOWNLOAD, true));
        mSwitchAutoDownload.setOnCheckedChangeListener(this);
        txtAppFolder = (TextView) findViewById(R.id.txtAppFolder);
        txtAppFolder.setText(FileHelper.getAppFolder().getPath());
        btnOpenAppFolder = (ViewGroup) findViewById(R.id.btnOpenAppFolder);
        btnOpenAppFolder.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        AppUtils.openAppFolder(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        TinyDB.getInstance(this).putBoolean(Constants.PREF_AUTO_DOWNLOAD, isChecked);
        if (isChecked) {
            AlarmServiceHelper.getInstance(this).start();
        } else {
            AlarmServiceHelper.getInstance(this).stop();
        }
    }
}
