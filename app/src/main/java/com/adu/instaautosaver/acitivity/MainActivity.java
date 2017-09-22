package com.adu.instaautosaver.acitivity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.adapter.PagerAdapter;
import com.adu.instaautosaver.fragment.DownloadFragment;
import com.adu.instaautosaver.fragment.HomeFragment;
import com.adu.instaautosaver.utils.AlarmServiceHelper;
import com.adu.instaautosaver.utils.AppConfigHelper;
import com.adu.instaautosaver.utils.AppUtils;
import com.adu.instaautosaver.utils.OSUtil;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PagerAdapter mPageAdapter;
    private final static int TAB_HOME = 0;
    private final static int TAB_DOWNLOAD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        startService();
        AppConfigHelper.update(this);
        showAdWall(AppConfigHelper.getInt(this, AppConfigHelper.CONF_KEY_AD_WALL_PRIMARY_RATE, AppConfigHelper.CONF_DEF_VAL_AD_WALL_PRIMARY_RATE));
    }


    private void startService() {
        AlarmServiceHelper.getInstance(this).start();
    }

    protected void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mPageAdapter = new PagerAdapter(getSupportFragmentManager());
        mPageAdapter.addFragment(new HomeFragment());
        mPageAdapter.addFragment(new DownloadFragment());
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.id_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(TAB_HOME).setCustomView(R.layout.tab_custom_item);
        mTabLayout.getTabAt(TAB_DOWNLOAD).setCustomView(R.layout.tab_custom_item);
        mViewPager.addOnPageChangeListener(this);
        focusTab(0);
    }

    private void focusTab(int position) {
        if (position == TAB_HOME) {
            setupTab(TAB_HOME, true);
            setupTab(TAB_DOWNLOAD, false);
        } else {
            setupTab(TAB_DOWNLOAD, true);
            setupTab(TAB_HOME, false);
        }
    }

    protected void setupTab(int position, boolean focus) {
        final View tabView = mTabLayout.getTabAt(position).getCustomView();
        final TextView tabTitle = (TextView) tabView.findViewById(R.id.txtTabTitle);
        final ImageView tabIcon = (ImageView) tabView.findViewById(R.id.imgTabIcon);
        if (position == 0) {
            tabTitle.setText(getResources().getString(R.string.tab_label_home));
            if (focus) {
                tabTitle.setTextColor(getResources().getColor(R.color.tab_color_text_focus));
                tabIcon.setImageResource(R.drawable.ic_home_white);
            } else {
                tabTitle.setTextColor(getResources().getColor(R.color.tab_color_text_gray));
                tabIcon.setImageResource(R.drawable.ic_home_gray);
            }

        } else {
            tabTitle.setText(getResources().getString(R.string.tab_label_download));
            if (focus) {
                tabTitle.setTextColor(getResources().getColor(R.color.tab_color_text_focus));
                tabIcon.setImageResource(R.drawable.ic_download_white);
            } else {
                tabTitle.setTextColor(getResources().getColor(R.color.tab_color_text_gray));
                tabIcon.setImageResource(R.drawable.ic_download_gray);
            }
        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mPageAdapter != null) {
            HomeFragment fragment = (HomeFragment) mPageAdapter.getItem(TAB_HOME);
            if (fragment != null) {
                mViewPager.setCurrentItem(TAB_HOME);
                fragment.onHandleNewIntent(intent);
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_like) {
            AppUtils.linkToGooglePlay(this, getPackageName());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        focusTab(position);
        if (position == TAB_HOME) {
            DownloadFragment fragment = (DownloadFragment) mPageAdapter.getItem(TAB_DOWNLOAD);
            if (fragment != null) {
                fragment.closeActionMode();
            }
        }
        if (position == TAB_DOWNLOAD) {
            DownloadFragment fragment = (DownloadFragment) mPageAdapter.getItem(TAB_DOWNLOAD);
            if (fragment != null) {
                fragment.loadMedia();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            final View view = getCurrentFocus();

            if (view != null) {
                final boolean consumed = super.dispatchTouchEvent(ev);

                final View viewTmp = getCurrentFocus();
                final View viewNew = viewTmp != null ? viewTmp : view;

                if (viewNew.equals(view)) {
                    final Rect rect = new Rect();
                    final int[] coordinates = new int[2];

                    view.getLocationOnScreen(coordinates);

                    rect.set(coordinates[0], coordinates[1], coordinates[0] + view.getWidth(), coordinates[1] + view.getHeight());

                    final int x = (int) ev.getX();
                    final int y = (int) ev.getY();

                    if (rect.contains(x, y)) {
                        return consumed;
                    }
                } else if (viewNew instanceof EditText) {
                    return consumed;
                }

                OSUtil.hideKeyboard(this);

                viewNew.clearFocus();

                return consumed;
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}
