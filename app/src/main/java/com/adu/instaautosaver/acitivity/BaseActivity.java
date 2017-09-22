package com.adu.instaautosaver.acitivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.entity.InstagMedia;
import com.adu.instaautosaver.utils.FileHelper;
import com.adu.instaautosaver.utils.PermissonHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Thomc on 19/02/2016.
 */
public class BaseActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    protected InterstitialAd mInterstitialAd;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    protected void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHomeScreen();
            }
        });
    }

    private void backToHomeScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    protected void loadAd() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    protected void showAdWall(int rate) {
        int random = new Random().nextInt(100);
        Log.d("", "showAdWall rate: " + rate + " random: " + random);
        if (random < rate) {
            showAdWall();
        }
    }

    protected void showAdWall() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.banner_ad_wall_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
            }

            public void onAdLoaded() {
                mInterstitialAd.show();
            }
        });
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    /**
     * Permission handle for Android M.
     */
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private boolean mCheckPermissionFlag;
    private boolean mShowingFlag;

    public void onStop() {
        super.onStop();
        mCheckPermissionFlag = false;
    }

    private void checkPermissions() {
        if (mCheckPermissionFlag == true || mShowingFlag == true) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        final List<String> permissionsList = PermissonHelper.getDisablePermission(this);
        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            mCheckPermissionFlag = true;
            return;
        }

    }

    private void onPermissionDenied(String permissionName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Permission Denied");
        builder.setMessage("Cannot run app with denied permission: [" + permissionName + "]\nPlease try it again!");
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.show();
        mShowingFlag = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                for (String permission : PermissonHelper.sRequiredPermissions) {
                    perms.put(permission, PackageManager.PERMISSION_GRANTED);
                }
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                int disableCount = 0;
                StringBuilder permissionDes = new StringBuilder();
                for (String permission : PermissonHelper.sRequiredPermissions) {
                    if (perms.get(permission) != PackageManager.PERMISSION_GRANTED) {
                        if (disableCount > 0) {
                            permissionDes.append(", ");
                        }
                        permissionDes.append(PermissonHelper.getDisablePermissionName(permission));
                        disableCount++;
                    }
                }
                if (disableCount == 0) {
                    // All Permissions Granted
                    Toast.makeText(this, "All Permissions Granted", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    if (disableCount == 5) {
                        onPermissionDenied(PermissonHelper.getDisablePermissionName("all"));
                    } else {
                        onPermissionDenied(permissionDes.toString());
                    }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
