package com.adu.instaautosaver.entity;

/**
 * Created by Thomc on 16/02/2016.
 */
public class InstagUser {
    private String id;
    private String mName;
    private String mProfileUrl;

    public InstagUser(String id, String name, String profile) {
        this.id = id;
        this.mName = name;
        this.mProfileUrl = profile;
    }

    public InstagUser() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getProfileUrl() {
        return mProfileUrl;
    }

    public void setProfileUrl(String mProfileUrl) {
        this.mProfileUrl = mProfileUrl;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id: " + id);
        builder.append("\n");
        builder.append("name: " + mName);
        builder.append("\n");
        builder.append("profile: " + mProfileUrl);
        return builder.toString();
    }
}
