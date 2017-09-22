package com.adu.instaautosaver.adapter;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.entity.InstagMedia;
import com.adu.instaautosaver.utils.ImageLoaderHelper;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.MultiSelectorBindingHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Thomc on 18/02/2016.
 */
public class PhotoGridAdapter extends BasePhotoAdapter {
    private static final String TAG = "PhotoGridAdapter";
    private MultiSelector mMultiSelector;
    private ActionMode.Callback mDeleteMode;
    private Activity mParent;
    private ActionMode mActionMode;

    public class ViewHolderItem extends MultiSelectorBindingHolder implements View.OnClickListener, View.OnLongClickListener {

        private final CheckBox mChkSelection;
        private final ImageView mImgPhoto;
        private final ImageView mImgPlay;

        public ViewHolderItem(View itemView) {
            super(itemView, mMultiSelector);
            mChkSelection = (CheckBox) itemView.findViewById(R.id.solvedCheckBox);
            mImgPhoto = (ImageView) itemView.findViewById(R.id.imgPhoto);
            mImgPlay = (ImageView) itemView.findViewById(R.id.imgPlay);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setLongClickable(true);
        }

        @Override
        public void onClick(View v) {
            if (mMultiSelector.tapSelection(this)) {
                Log.d(TAG, "select");
                // Selection is on, so tapSelection() toggled item selection.
            } else {
                if (mMediaItemClickListener != null) {
                    mMediaItemClickListener.onViewAction(v, getItemAt(getAdapterPosition()));
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            AppCompatActivity activity = (AppCompatActivity) mParent;
            mActionMode = activity.startSupportActionMode(mDeleteMode);
            mMultiSelector.setSelected(this, true);
            return true;
        }


        @Override
        public void setSelectable(boolean b) {
            if (b) {
                mChkSelection.setVisibility(View.VISIBLE);
            } else {
                mChkSelection.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean isSelectable() {
            return false;
        }

        @Override
        public void setActivated(boolean b) {
            if (b) {
                mChkSelection.setChecked(true);
            } else {
                mChkSelection.setChecked(false);
            }
        }

        @Override
        public boolean isActivated() {
            return false;
        }
    }

    public PhotoGridAdapter(Activity parent, List<InstagMedia> items, OnHeaderClickListener onHeaderClickListener) {
        super(parent, items, onHeaderClickListener);
        this.mParent = parent;

    }


    public void closeActionMode() {
        if (mMultiSelector.isSelectable()) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
            mMultiSelector.setSelectable(false);
        }
    }

    public void setMultiSelector(MultiSelector mMultiSelector) {
        this.mMultiSelector = mMultiSelector;
    }

    public void setDeleteMode(ActionMode.Callback mDeleteMode) {
        this.mDeleteMode = mDeleteMode;
    }

    public MultiSelector getMultiSelector() {
        return mMultiSelector;
    }

    public ActionMode.Callback getDeleteMode() {
        return mDeleteMode;
    }

    @Override
    protected RecyclerView.ViewHolder getViewItem(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_grid_item, parent, false);
        return new ViewHolderItem(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            InstagMedia item = getItemAt(position);
            ImageLoader.getInstance().displayImage(item.getLocalFileUrl(), viewHolderItem.mImgPhoto, ImageLoaderHelper.DEFAULT_OPTIONS, ImageLoaderHelper.DEFAULT_LISTENER);
            if (item.isIsPhoto()) {
                viewHolderItem.mImgPlay.setVisibility(View.GONE);
            } else {
                viewHolderItem.mImgPlay.setVisibility(View.VISIBLE);
            }
        }
    }


}
