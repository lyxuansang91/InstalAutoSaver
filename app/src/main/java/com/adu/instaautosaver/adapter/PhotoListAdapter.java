package com.adu.instaautosaver.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.db.DatabaseHelper;
import com.adu.instaautosaver.entity.InstagMedia;
import com.adu.instaautosaver.entity.InstagUser;
import com.adu.instaautosaver.utils.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Thomc on 18/02/2016.
 */
public class PhotoListAdapter extends BasePhotoAdapter {
    public class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mImgPhoto;
        public final ImageView mImgPlay;
        public final ImageView mImgOwnerProfile;
        public final TextView mTxtOwnerName;
        public final ImageView mImgMenu;

        public ViewHolderItem(View view) {
            super(view);
            mImgPhoto = (ImageView) view.findViewById(R.id.imgPhoto);
            mImgPhoto.setOnClickListener(this);
            mImgPlay = (ImageView) view.findViewById(R.id.imgPlay);
            mImgPlay.setOnClickListener(this);
            mImgOwnerProfile = (ImageView) view.findViewById(R.id.imgOwnerProfile);
            mImgOwnerProfile.setOnClickListener(this);
            mTxtOwnerName = (TextView) view.findViewById(R.id.txtOwnerName);
            mTxtOwnerName.setOnClickListener(this);
            mImgMenu = (ImageView) view.findViewById(R.id.imgMenu);
            mImgMenu.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mMediaItemClickListener != null) {
                mMediaItemClickListener.onSingleMediaItemClick(v, getItemAt(getAdapterPosition()), getAdapterPosition());
            }
        }
    }

    public PhotoListAdapter(Context context, List<InstagMedia> items, OnHeaderClickListener onHeaderClickListener) {
        super(context, items, onHeaderClickListener);
    }


    @Override
    protected RecyclerView.ViewHolder getViewItem(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_cardview_item, parent, false);
        return new ViewHolderItem(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            InstagMedia item = getItemAt(position);
            InstagUser owner = item.getOwner();
            if (owner != null && owner.getProfileUrl() == null) {
                InstagUser user = DatabaseHelper.getInstance(getContext()).getUserById(owner.getId());
                if (user != null) {
                    owner.setProfileUrl(user.getProfileUrl());
                } else {
                    owner.setProfileUrl("");
                }
            }
            ImageLoader.getInstance().displayImage(item.getLocalFileUrl(), viewHolderItem.mImgPhoto, ImageLoaderHelper.DEFAULT_OPTIONS, ImageLoaderHelper.DEFAULT_LISTENER);
            if (item.isIsPhoto()) {
                viewHolderItem.mImgPlay.setVisibility(View.GONE);
            } else {
                viewHolderItem.mImgPlay.setVisibility(View.VISIBLE);
            }
            ImageLoader.getInstance().displayImage(owner.getProfileUrl(), viewHolderItem.mImgOwnerProfile, ImageLoaderHelper.DEFAULT_OPTIONS, ImageLoaderHelper.DEFAULT_LISTENER);
            viewHolderItem.mTxtOwnerName.setText(owner.getName());
        }
    }


}
