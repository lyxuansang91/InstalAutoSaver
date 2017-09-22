package com.adu.instaautosaver.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adu.instaautosaver.R;
import com.adu.instaautosaver.entity.InstagMedia;

import java.util.List;

/**
 * Created by Thomc on 18/02/2016.
 */
public abstract class BasePhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected static final int VIEW_TYPE_HEADER = 0;
    protected static final int VIEW_TYPE_ITEM = 1;
    private final Context mContext;
    protected OnHeaderClickListener mOnHeaderClickListener;
    final List<InstagMedia> mItems;
    protected OnSingleMediaItemClickListener mMediaItemClickListener;

    public class ViewHolderHeader extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final ImageView mImgList;
        public final ImageView mImgGrid;

        public ViewHolderHeader(View view) {
            super(view);
            mView = view;
            mImgList = (ImageView) view.findViewById(R.id.imgList);
            mImgGrid = (ImageView) view.findViewById(R.id.imgGrid);
            mImgList.setOnClickListener(this);
            mImgGrid.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnHeaderClickListener != null) {
                if (v == mImgList) {
                    mOnHeaderClickListener.onListViewClick(v);
                } else if (v == mImgGrid) {
                    mOnHeaderClickListener.onGridViewClick(v);
                }
            }
        }
    }


    public BasePhotoAdapter(Context context, List<InstagMedia> items, OnHeaderClickListener onHeaderClickListener) {
        this.mContext = context;
        mItems = items;
        this.mOnHeaderClickListener = onHeaderClickListener;
    }

    public void setMediaItemClickListener(OnSingleMediaItemClickListener mMediaItemClickListener) {
        this.mMediaItemClickListener = mMediaItemClickListener;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_header_item, parent, false);
            return new ViewHolderHeader(view);
        } else {
            return getViewItem(parent);
        }
    }

    protected abstract RecyclerView.ViewHolder getViewItem(ViewGroup parent);

    public void onListTypeChanged() {
        notifyItemChanged(VIEW_TYPE_HEADER);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            ViewHolderHeader holderHeader = (ViewHolderHeader) holder;
            if (this instanceof PhotoListAdapter) {
                holderHeader.mImgList.setImageResource(R.drawable.ic_list_focus);
                holderHeader.mImgGrid.setImageResource(R.drawable.ic_grid);
            } else if (this instanceof PhotoGridAdapter) {
                holderHeader.mImgGrid.setImageResource(R.drawable.ic_grid_focus);
                holderHeader.mImgList.setImageResource(R.drawable.ic_list);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size() + 1;
    }

    public InstagMedia getItemAt(int position) {
        if (position > 0)
            return mItems.get(position - 1);
        else
            return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_TYPE_HEADER;
        return VIEW_TYPE_ITEM;
    }

    public interface OnHeaderClickListener {
        public void onListViewClick(View v);

        public void onGridViewClick(View v);
    }

    public interface OnSingleMediaItemClickListener {
        public void onSingleMediaItemClick(View v, InstagMedia item, int position);

        public void onViewAction(View v, InstagMedia item);
    }
}
