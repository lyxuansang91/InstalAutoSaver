package com.adu.instaautosaver.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomc on 11/05/2016.
 */
public class PermissonHelper {
    public static final String[] sRequiredPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    public static final String[] sRequiredPermissionNames = new String[]{
            "Storage", "All"
    };

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean addPermission(Context context, List<String> permissionsList, String permission) {
        Log.d("thomc", "addPermission: " + permission);
        if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
//            if (!activity.shouldShowRequestPermissionRationale(permission))
            return false;
        }
        return true;
    }

    public static boolean isAllPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return getDisablePermission(context).isEmpty();
    }

    public static String getDisablePermissionName(String permission) {
        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission))
            return sRequiredPermissionNames[0];
        else
            return sRequiredPermissionNames[1];
    }

    public static List<String> getDisablePermission(Context context) {
        List<String> res = new ArrayList<>();
        for (String permission : sRequiredPermissions) {
            addPermission(context, res, permission);
        }
        return res;
    }
}
