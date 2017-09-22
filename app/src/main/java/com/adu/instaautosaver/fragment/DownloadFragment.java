package com.adu.instaautosaver.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.acitivity.PhotoViewActivity;
import com.adu.instaautosaver.acitivity.VideoViewActivity;
import com.adu.instaautosaver.adapter.BasePhotoAdapter;
import com.adu.instaautosaver.adapter.PhotoGridAdapter;
import com.adu.instaautosaver.adapter.PhotoListAdapter;
import com.adu.instaautosaver.constant.ExtraBundleKeyConstants;
import com.adu.instaautosaver.entity.InstagMedia;
import com.adu.instaautosaver.utils.AppUtils;
import com.adu.instaautosaver.utils.FileHelper;
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomc on 18/02/2016.
 */
public class DownloadFragment extends Fragment implements BasePhotoAdapter.OnHeaderClickListener, SwipeRefreshLayout.OnRefreshListener, PhotoListAdapter.OnSingleMediaItemClickListener {
    private View mRootView;
    private RecyclerView listViewPhoto;
    private RecyclerView gridViewPhoto;
    private MultiSelector mMultiSelector = new MultiSelector();
    private List<InstagMedia> mItems;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(
                R.layout.fragment_download, container, false);
        initViews();
        return mRootView;
    }

    private void initViews() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        listViewPhoto = (RecyclerView) mRootView.findViewById(R.id.listViewPhoto);
        listViewPhoto.setLayoutManager(new LinearLayoutManager(getContext()));
        gridViewPhoto = (RecyclerView) mRootView.findViewById(R.id.gridViewPhoto);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) return 3;
                else
                    return 1;
            }
        });
        gridViewPhoto.setLayoutManager(gridLayoutManager);
    }

    public void loadMedia() {
        new AsyncTask<Void, Void, List<InstagMedia>>() {
            @Override
            protected List<InstagMedia> doInBackground(Void... params) {
                return FileHelper.listSavedMedia();
            }

            @Override
            protected void onPostExecute(List<InstagMedia> result) {
                mItems = result;
                if (listViewPhoto != null) {
                    PhotoListAdapter listAdapter = new PhotoListAdapter(getActivity(), result, DownloadFragment.this);
                    listAdapter.setMediaItemClickListener(DownloadFragment.this);
                    listViewPhoto.setAdapter(listAdapter);
                }
                if (gridViewPhoto != null) {
                    PhotoGridAdapter gridAdapter = new PhotoGridAdapter(getActivity(), result, DownloadFragment.this);
                    gridAdapter.setMediaItemClickListener(DownloadFragment.this);
                    gridViewPhoto.setAdapter(gridAdapter);
                    gridAdapter.setMultiSelector(mMultiSelector);
                    gridAdapter.setDeleteMode(mDeleteMode);
                    mMultiSelector.setSelectable(false);
                }
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }.execute();
    }

    private ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            super.onCreateActionMode(actionMode, menu);
            getActivity().getMenuInflater().inflate(R.menu.menu_media_mutil_action, menu);
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            super.onDestroyActionMode(mode);
            PhotoGridAdapter adapter = (PhotoGridAdapter) gridViewPhoto.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.media_action_delete:
                    if (mMultiSelector.getSelectedPositions().isEmpty()) {
                        actionMode.finish();
                    } else {
                        confirmDelete(actionMode, mMultiSelector.getSelectedPositions());
                    }
                    break;
                case R.id.media_action_share:
                    if (mMultiSelector.getSelectedPositions().isEmpty()) {
                        actionMode.finish();
                    } else {
                        shareMedia(actionMode, mMultiSelector.getSelectedPositions());
                    }
                    break;
                default:
                    actionMode.finish();
                    break;
            }

            return false;
        }
    };

    private void shareMedia(final ActionMode actionMode, final List<Integer> selectedPositions) {
        actionMode.finish();
        List<InstagMedia> shareItems = new ArrayList<InstagMedia>();
        for (int pos : selectedPositions) {
            shareItems.add(mItems.get(pos - 1));
        }
        ArrayList<Uri> uriList = new ArrayList<>();
        for (InstagMedia item : shareItems) {
            uriList.add(Uri.parse(item.getLocalFileUrl()));
        }
        Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sendIntent.setType("image/*");
        sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        startActivity(Intent.createChooser(sendIntent, "Share photo"));
        mMultiSelector.clearSelections();
    }


    private void confirmDelete(final ActionMode actionMode, final List<Integer> positions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Delete photo");
        builder.setMessage(getString(R.string.msg_confirm_delete_photo));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (actionMode != null) {
                    actionMode.finish();
                }
                List<InstagMedia> deleteItems = new ArrayList<InstagMedia>();
                for (int pos : positions) {
                    Log.d("selected: ", "" + pos + "\n");
                    deleteItems.add(mItems.get(pos - 1));
                }

                for (InstagMedia item : deleteItems) {
                    boolean deleted = FileHelper.deleteMedia(item.getLocalFileUrl());
                    if (deleted) {
                        mItems.remove(item);
                    }
                }
                int oneItemDeletePosition = -1;
                if (positions.size() == 1) {
                    oneItemDeletePosition = positions.get(0);
                }
                if (listViewPhoto != null) {
                    RecyclerView.Adapter adapterList = listViewPhoto.getAdapter();
                    if (adapterList != null) {
                        if (oneItemDeletePosition != -1) {
                            adapterList.notifyItemRemoved(oneItemDeletePosition);
                        } else {
                            adapterList.notifyDataSetChanged();
                        }
                    }

                }
                if (gridViewPhoto != null) {
                    RecyclerView.Adapter adapterGrid = gridViewPhoto.getAdapter();
                    if (adapterGrid != null) {
                        if (oneItemDeletePosition != -1) {
                            adapterGrid.notifyItemRemoved(oneItemDeletePosition);
                        } else {
                            adapterGrid.notifyDataSetChanged();
                        }
                    }
                }
                mMultiSelector.clearSelections();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void closeActionMode() {
        if (gridViewPhoto != null) {
            PhotoGridAdapter adapter = (PhotoGridAdapter) gridViewPhoto.getAdapter();
            if (adapter != null) {
                adapter.closeActionMode();
            }
        }
    }

    @Override
    public void onListViewClick(View v) {
        listViewPhoto.setVisibility(View.VISIBLE);
        gridViewPhoto.setVisibility(View.GONE);
        ((BasePhotoAdapter) listViewPhoto.getAdapter()).onListTypeChanged();
        closeActionMode();
    }

    @Override
    public void onGridViewClick(View v) {
        gridViewPhoto.setVisibility(View.VISIBLE);
        listViewPhoto.setVisibility(View.GONE);
        ((BasePhotoAdapter) gridViewPhoto.getAdapter()).onListTypeChanged();
    }

    private void showSingleMediaActionMenu(final int position, final InstagMedia media, View v) {
        Context wrapper = new ContextThemeWrapper(getContext(), R.style.MediaActionPopupTheme);
        PopupMenu popup = new PopupMenu(wrapper, v);
        if (media.isIsPhoto()) {
            popup.getMenuInflater().inflate(R.menu.menu_media_single_photo_action,
                    popup.getMenu());
        } else {
            popup.getMenuInflater().inflate(R.menu.menu_media_single_video_action,
                    popup.getMenu());
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.media_action_delete:
                        List<Integer> deleteItems = new ArrayList<>();
                        deleteItems.add(position);
                        confirmDelete(null, deleteItems);
                        break;
                    case R.id.media_action_set_as:
                        Intent setAsIntent = new Intent(Intent.ACTION_ATTACH_DATA);
                        setAsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        setAsIntent.setDataAndType(Uri.parse(media.getLocalFileUrl()), "image/jpeg");
                        setAsIntent.putExtra("mimeType", "image/jpeg");
                        startActivity(Intent.createChooser(setAsIntent, "Set as:"));
                        break;
                    case R.id.media_action_share:
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.setType("image/*");
                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(media.getLocalFileUrl()));
                        startActivity(Intent.createChooser(sendIntent, "Share photo"));
                        break;
                    case R.id.media_action_open_with:
                        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                        if (media.isIsPhoto()) {
                            viewIntent.setDataAndType(Uri.parse(media.getLocalFileUrl()), "image/*");
                        } else {
                            viewIntent.setDataAndType(Uri.parse(media.getLocalFileUrl()), "video/*");
                        }
                        startActivity(viewIntent);
                        break;
                    case R.id.media_action_view_on_instagram:
                        AppUtils.openInstagramPost(getContext(), media.getId());
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onRefresh() {
        closeActionMode();
        loadMedia();
    }

    private void openMedia(InstagMedia media) {
        Intent intent = null;
        if (media.isIsPhoto()) {
            intent = new Intent(getContext(), PhotoViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ExtraBundleKeyConstants.EXTRA_MEDIA_URL, media.getLocalFileUrl());
            intent.putExtras(bundle);
        } else {
            intent = new Intent(getContext(), VideoViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ExtraBundleKeyConstants.EXTRA_MEDIA_URL, media.getLocalFileUrl());
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @Override
    public void onSingleMediaItemClick(View v, InstagMedia item, int position) {
        switch (v.getId()) {
            case R.id.imgMenu:
                showSingleMediaActionMenu(position, item, v);
                break;
            case R.id.imgPhoto:
            case R.id.imgPlay:
                openMedia(item);
                break;
            case R.id.imgOwnerProfile:
            case R.id.txtOwnerName:
                AppUtils.openInstagramUser(getContext(), item.getOwner().getName());
                break;
        }
    }

    @Override
    public void onViewAction(View v, InstagMedia item) {
        openMedia(item);
    }
}