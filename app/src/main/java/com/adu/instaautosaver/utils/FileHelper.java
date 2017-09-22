package com.adu.instaautosaver.utils;

import android.os.Environment;

import com.adu.instaautosaver.entity.InstagMedia;
import com.adu.instaautosaver.entity.InstagUser;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Thomc on 17/02/2016.
 */
public class FileHelper {
    // folder
    public static final String APP_FOLDER_NAME = "InstaAutoSaver";
    public static String APP_FOLDER_PATH = "";
    private static final List<String> sMEDIA_FILE_EXTENDS = Arrays.asList(new String[]{".jpg", "jpge", ".mp4"});
    public static final String FILE_PREFIX = "file://";
    public static final String FILE_NAME_SPACE = "instag";

    public static File getAppFolder() {
        File baseDir;
        baseDir = Environment.getExternalStorageDirectory();
        if (baseDir == null) {
            APP_FOLDER_PATH = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            return Environment.getExternalStorageDirectory();
        }

        File appFolder = new File(baseDir, APP_FOLDER_NAME);

        if (appFolder.exists()) {
            APP_FOLDER_PATH = appFolder.getAbsolutePath();
            return appFolder;
        }
        if (appFolder.mkdirs())
            return appFolder;
        APP_FOLDER_PATH = appFolder.getAbsolutePath();
        return Environment.getExternalStorageDirectory();
    }

    public static File findSavedMediaByName(String fileName) {
        File file = null;
        try {
            final File dir = new File(getAppFolder().getPath());
            FileNameFilter filter = new FileNameFilter(fileName);
            File[] files = dir.listFiles(filter);
            if (files != null && file.length() > 0) {
                file = files[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static List<InstagMedia> listSavedMedia() {
        List<InstagMedia> result = new ArrayList<InstagMedia>();
        try {
            final File dir = new File(getAppFolder().getPath());
            // String rootStr = dir.getAbsolutePath() + "/";
            FileExtensionFilter filter = new FileExtensionFilter(sMEDIA_FILE_EXTENDS);

            File[] files = dir.listFiles(filter);
            sortFiles(files);
            for (int i = 0; i < files.length; i++) {
                try {
                    InstagMedia media = new InstagMedia();
                    InstagUser user = new InstagUser();
                    String fileName = files[i].getName();
                    user.setName(fileName.substring(0, fileName.indexOf(FILE_NAME_SPACE)));
                    user.setId(fileName.substring(fileName.indexOf(FILE_NAME_SPACE) + FILE_NAME_SPACE.length(),
                            fileName.lastIndexOf(FILE_NAME_SPACE)));
                    media.setId(fileName.substring(fileName.lastIndexOf(FILE_NAME_SPACE) + FILE_NAME_SPACE.length(),
                            fileName.lastIndexOf(".")));
                    media.setLocalFileUrl(FILE_PREFIX + files[i].getAbsolutePath());
                    media.setOwner(user);
                    if (fileName.endsWith(".mp4")) {
                        media.setIsPhoto(false);
                    } else {
                        media.setIsPhoto(true);
                    }
                    result.add(media);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void sortFiles(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File lhs, File rhs) {
                if (lhs.lastModified() < rhs.lastModified()) return 1;
                return -1;
            }
        });
    }

    public static boolean deleteMedia(String fileName) {
        try {
            if (fileName != null && fileName.startsWith(FILE_PREFIX)) {
                fileName = fileName.replace(FILE_PREFIX, "");
            }
            final File f = new File(fileName);
            return f.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static class FileNameFilter implements FilenameFilter {
        private String mName;

        public FileNameFilter(String name) {
            this.mName = name;
        }

        public boolean accept(File dir, String name) {
            if (name.equals(mName)) return true;
            return false;
        }
    }

    static class FileExtensionFilter implements FilenameFilter {
        private List<String> mExtendList;

        public FileExtensionFilter(List<String> list) {
            this.mExtendList = list;
        }

        public boolean accept(File dir, String name) {
            for (String ext : mExtendList) {
                if (name.endsWith(ext)) return true;
            }
            return false;
        }
    }
}
