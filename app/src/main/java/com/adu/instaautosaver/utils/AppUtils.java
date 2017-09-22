package com.adu.instaautosaver.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.adu.instaautosaver.constant.Constants;

/**
 * Created by Thomc on 20/02/2016.
 */
public class AppUtils {
    public static boolean isInstagramInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(Constants.INSTAG_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void openInstagramUser(Context context, String username) {
        Uri uri = Uri.parse("http://instagram.com/_u/" + username);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        likeIng.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        likeIng.setPackage(Constants.INSTAG_PACKAGE_NAME);
        try {
            context.startActivity(likeIng);
        } catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/" + username)));
        }
    }

    public static void openInstagramPost(Context context, String postId) {
        Uri uri = Uri.parse("http://instagram.com/p/" + postId);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        likeIng.setPackage(Constants.INSTAG_PACKAGE_NAME);
        try {
            context.startActivity(likeIng);
        } catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/p/" + postId)));
        }
    }

    public static void linkToGooglePlay(Context context, String appPackageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void openAppFolder(Context context) {
        Uri selectedUri = Uri.parse(FileHelper.getAppFolder().getPath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");
        context.startActivity(Intent.createChooser(intent, "Open folder"));
    }

    public static void watchYoutubeVideo(Context context, String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            context.startActivity(intent);
        }
    }
}
