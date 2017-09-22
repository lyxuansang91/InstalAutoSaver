package com.adu.instaautosaver.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.acitivity.PhotoViewActivity;
import com.adu.instaautosaver.acitivity.VideoViewActivity;
import com.adu.instaautosaver.constant.Constants;
import com.adu.instaautosaver.constant.ExtraBundleKeyConstants;
import com.adu.instaautosaver.db.DatabaseHelper;
import com.adu.instaautosaver.download.DownloadProgressCallback;
import com.adu.instaautosaver.download.InstagDownload;
import com.adu.instaautosaver.entity.InstagMedia;
import com.adu.instaautosaver.entity.InstagUser;
import com.adu.instaautosaver.utils.AppConfigHelper;
import com.adu.instaautosaver.utils.AppUtils;
import com.adu.instaautosaver.utils.ImageLoaderHelper;
import com.adu.instaautosaver.utils.InstagUrlParser;
import com.adu.instaautosaver.utils.NotificationHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by Thomc on 18/02/2016.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    private EditText edtURL;
    private ViewGroup btnOpenInstagram;
    private ViewGroup btnLoadPhoto;
    private ImageView btnPlay;
    private ImageView imgPhoto;
    private ImageView imgOwnerProfile;
    private TextView txtOwnerName;
    private ImageView imgDownload;
    private CardView cardViewContent;
    private InstagMedia mCurrentMedia;
    private ProgressDialog mProgressDialog;
    private ProgressDialog mProgressDownloadDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(
                R.layout.fragment_home, container, false);
        initViews();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(false);
        Intent intent = getActivity().getIntent();
        onHandleNewIntent(intent);
        if (AppConfigHelper.getBoolean(getContext(), AppConfigHelper.CONF_KEY_AD_BANNER_PRIMARY_ENABLE, AppConfigHelper.CONF_DEF_VAL_AD_BANNER_PRIMARY_ENABLE)) {
            loadAd();
        }

    }

    public void onHandleNewIntent(Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String url = bundle.getString(ExtraBundleKeyConstants.EXTRA_NOTIFICATION_URL);
                if (!TextUtils.isEmpty(url)) {
                    NotificationHelper.closeConfirmNotification(getActivity());
                    handleUrl(url);
                }
                intent.putExtras(new Bundle());
            }
        }
    }

    private void initViews() {
        loadUi();
        setupListener();
    }

    private void loadAd() {
        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        mAdView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void loadUi() {
        edtURL = (EditText) rootView.findViewById(R.id.editUrl);
        btnOpenInstagram = (ViewGroup) rootView.findViewById(R.id.btnOpenInstagram);
        btnLoadPhoto = (ViewGroup) rootView.findViewById(R.id.btnLoadPhoto);
        imgPhoto = (ImageView) rootView.findViewById(R.id.imgPhoto);
        btnPlay = (ImageView) rootView.findViewById(R.id.imgPlay);
        imgOwnerProfile = (ImageView) rootView.findViewById(R.id.imgOwnerProfile);
        txtOwnerName = (TextView) rootView.findViewById(R.id.txtOwnerName);
        imgDownload = (ImageView) rootView.findViewById(R.id.imgDownload);
        cardViewContent = (CardView) rootView.findViewById(R.id.cardView);
    }

    private void setupListener() {
        imgPhoto.setOnClickListener(this);
        btnOpenInstagram.setOnClickListener(this);
        btnLoadPhoto.setOnClickListener(this);
        txtOwnerName.setOnClickListener(this);
        imgOwnerProfile.setOnClickListener(this);
        imgDownload.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
    }

    private void handleUrl(String url) {
        edtURL.setText(url);
        edtURL.requestFocus();
        loadMediaContent(url);
    }

    private void loadMediaContent(String url) {
        showProgress();
        InstagUrlParser.parseUrl(url, new InstagUrlParser.InstagUrlParserCallback() {
            @Override
            public void onResults(InstagMedia result) {
                if (result == null) {
                    Toast.makeText(getContext(), getResources().getString(R.string.msg_can_not_load_photo_content), Toast.LENGTH_SHORT).show();
                    hideProgress();
                } else {
                    if (getContext() != null) {
                        showMediaContent(result);
                    }
                }
            }
        });
    }

    private void showMediaContent(InstagMedia media) {
        mCurrentMedia = media;
        resetOnLoadedContent();
        cardViewContent.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().displayImage(media.getThumbUrl(), imgPhoto, ImageLoaderHelper.DEFAULT_OPTIONS, new PhotoLoadingListener());
        ImageLoader.getInstance().displayImage(media.getOwner().getProfileUrl(), imgOwnerProfile, ImageLoaderHelper.DEFAULT_OPTIONS, ImageLoaderHelper.DEFAULT_LISTENER);
        txtOwnerName.setText(media.getOwner().getName());
        if (media.isIsPhoto()) {
            btnPlay.setVisibility(View.GONE);
        } else {
            btnPlay.setVisibility(View.VISIBLE);
        }
    }

    private void onLoadPhoto() {
        String str = edtURL.getText().toString().trim();
        if (TextUtils.isEmpty(str) || !(str.startsWith(Constants.INSTAG_HTTPS_LINK_PREFIX) || str.startsWith(Constants.INSTAG_HTTP_LINK_PREFIX))) {
            Toast.makeText(getContext(), getResources().getString(R.string.msg_url_blank), Toast.LENGTH_LONG).show();
        } else {
            loadMediaContent(edtURL.getText().toString().trim());
        }

    }

    private void resetOnLoadedContent() {
        cardViewContent.setVisibility(View.INVISIBLE);
        imgPhoto.setImageBitmap(null);
        imgPhoto.setBackgroundColor(getResources().getColor(R.color.photo_bg_color));
        imgOwnerProfile.setImageBitmap(null);
        txtOwnerName.setText("");
    }

    private void openInstagram() {
        if (AppUtils.isInstagramInstalled(getContext())) {
            try {
                PackageManager pm = getContext().getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage(Constants.INSTAG_PACKAGE_NAME);
                getContext().startActivity(launchIntent);
            } catch (Exception ex) {
                Toast.makeText(getContext(), getResources().getString(R.string.msg_can_not_open_instagram), Toast.LENGTH_SHORT).show();

            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Install Instagram");
            builder.setMessage(getString(R.string.msg_confirm_istall_instagram));
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    AppUtils.linkToGooglePlay(getContext(), Constants.INSTAG_PACKAGE_NAME);
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }

    }

    private void openMedia() {
        Intent intent = null;
        if (mCurrentMedia.isIsPhoto()) {
            intent = new Intent(getContext(), PhotoViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ExtraBundleKeyConstants.EXTRA_MEDIA_URL, mCurrentMedia.getDownloadUrl());
            intent.putExtras(bundle);
        } else {
            intent = new Intent(getContext(), VideoViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ExtraBundleKeyConstants.EXTRA_MEDIA_URL, mCurrentMedia.getDownloadUrl());
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    private void onDownload() {
        if (mCurrentMedia != null) {
            showDownloadProgress();
            new AsyncTask<Void, Integer, Object>() {
                final long MAX_ELAPSED_TIME_UPDATE_PROGRESS = 500;
                private long mLastTimeUpdated;

                @Override
                protected Object doInBackground(Void... params) {
                    boolean res = InstagDownload.downloadAndSave(mCurrentMedia.getDownloadUrl(), mCurrentMedia.buildLocalFileName(), new DownloadProgressCallback() {
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
                    updateDownloadProgress(values[0]);
                }

                @Override
                protected void onPostExecute(Object result) {
                    hideDownloadProgress();
                    if (result != null) {
                        Toast.makeText(getContext(), getResources().getString(R.string.msg_download_completed), Toast.LENGTH_LONG).show();
                        try {
                            InstagUser user = mCurrentMedia.getOwner();
                            DatabaseHelper.getInstance(getContext()).updatePageData(user);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.msg_download_failure), Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }
    }


    @Override
    public void onClick(View v) {
        if (v == btnOpenInstagram) {
            openInstagram();
        } else if (v == btnLoadPhoto) {
            onLoadPhoto();
        } else if (v == imgDownload) {
            onDownload();
        } else if (v == txtOwnerName || v == imgOwnerProfile) {
            if (mCurrentMedia != null) {
                AppUtils.openInstagramUser(getContext(), mCurrentMedia.getOwner().getName());
            }
        } else if (v == btnPlay || v == imgPhoto) {
            openMedia();
        }
    }

    private void showDownloadProgress() {
        mProgressDownloadDialog = new ProgressDialog(getContext());
        mProgressDownloadDialog.setMessage("Downloading...");
        mProgressDownloadDialog.setCancelable(false);
        mProgressDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDownloadDialog.setProgress(0);
        mProgressDownloadDialog.setMax(100);
        try {
            mProgressDownloadDialog.show();
        } catch (Exception ex) {
        }
    }

    private void updateDownloadProgress(int progress) {
        if (mProgressDownloadDialog != null) {
            try {
                mProgressDownloadDialog.setProgress(progress);
            } catch (Exception ex) {
            }
        }
    }

    private void hideDownloadProgress() {
        if (mProgressDownloadDialog != null) {
            try {
                mProgressDownloadDialog.dismiss();
            } catch (Exception ex) {
            }
        }
    }

    private void showProgress() {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Wait a minute...");
        mProgressDialog.setCancelable(true);
        try {
            mProgressDialog.show();
        } catch (Exception ex) {
        }
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception ex) {
            }
        }
    }

    class PhotoLoadingListener extends
            SimpleImageLoadingListener {
        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            hideProgress();
        }


        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            hideProgress();
        }

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            hideProgress();
            ImageView imageView = (ImageView) view;
            if (getContext() != null) {
                if (loadedImage != null) {
                    imageView.setBackgroundColor(HomeFragment.this.getResources().getColor(R.color.transparent));
                    imageView.setImageBitmap(loadedImage);
                    FadeInBitmapDisplayer.animate(imageView, 500);
                } else {
                    imageView.setImageBitmap(null);
                    imageView.setBackgroundColor(HomeFragment.this.getResources().getColor(R.color.photo_bg_color));
                }
            } else {
                if (loadedImage != null) {
                    loadedImage.recycle();
                }
            }
        }

    }
}