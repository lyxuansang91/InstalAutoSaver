package com.adu.instaautosaver.entity;

import com.adu.instaautosaver.utils.FileHelper;

/**
 * Created by Thomc on 16/02/2016.
 */
public class InstagMedia {
    private String mId;
    private String mThumbUrl;
    private String mDownloadUrl;
    private String mCaption;
    private InstagUser mOwner;
    private boolean mIsPhoto;
    private String mLocalFileUrl;

    public InstagMedia(String id, String downloadUrl, String caption, InstagUser owner, boolean isPhoto) {
        this.mId = id;
        this.mDownloadUrl = downloadUrl;
        this.mCaption = caption;
        this.mOwner = owner;
        this.mIsPhoto = isPhoto;
    }

    public InstagMedia() {

    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public String getThumbUrl() {
        return mThumbUrl;
    }

    public void setThumbUrl(String mThumbUrl) {
        this.mThumbUrl = mThumbUrl;
    }

    public String getLocalFileUrl() {
        return mLocalFileUrl;
    }

    public void setLocalFileUrl(String mLocalFileUrl) {
        this.mLocalFileUrl = mLocalFileUrl;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    public void setUrl(String mUrl) {
        this.mDownloadUrl = mUrl;
    }

    public InstagUser getOwner() {
        return mOwner;
    }

    public void setOwner(InstagUser mOwner) {
        this.mOwner = mOwner;
    }

    public boolean isIsPhoto() {
        return mIsPhoto;
    }

    public void setIsPhoto(boolean mIsPhoto) {
        this.mIsPhoto = mIsPhoto;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id: " + mId);
        builder.append("\n");
        builder.append("download url: " + mDownloadUrl);
        builder.append("\n");
        builder.append("caption: " + mCaption);
        builder.append("\n");
        builder.append("owner: " + mOwner.toString());
        return builder.toString();
    }

    public String buildLocalFileName() {
        StringBuilder builder = new StringBuilder();
        builder.append(mOwner.getName());
        builder.append(FileHelper.FILE_NAME_SPACE);
        builder.append(mOwner.getId());
        builder.append(FileHelper.FILE_NAME_SPACE);
        builder.append(mId);
        if (isIsPhoto()) {
            builder.append(".jpg");
        } else {
            builder.append(".mp4");
        }
        return builder.toString();
    }
}
