package com.adu.instaautosaver.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.adu.instaautosaver.entity.InstagUser;
import com.adu.instaautosaver.utils.FileHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String dbName = FileHelper.getAppFolder().getAbsolutePath()
            + "/data/data.db";
    private static final String mUserTable = "instag_user";
    // tag comlumns
    private static final String mId = "user_id";
    private static final String mUseName = "user_name";
    private static final String mUserAvatar = "user_avatar";
    private static final String TAG = "DatabaseHelper";
    private static DatabaseHelper sInstance;

    public static DatabaseHelper getInstance(Context context) {
        if (sInstance == null)
            sInstance = new DatabaseHelper(context);
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, dbName, null, 1);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE " + mUserTable + " (" + mId
                + " TEXT, " + mUseName
                + " TEXT, " + mUserAvatar
                + " TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    public void clearDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + mUserTable);
        onCreate(db);
    }

    public InstagUser getUserById(String id) {
        InstagUser user = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * from " + mUserTable + " where "
                        + mId + " like ?",
                new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            user = new InstagUser(c.getString(c.getColumnIndex(mId)),
                    c.getString(c.getColumnIndex(mUseName)),
                    c.getString(c.getColumnIndex(mUserAvatar)));
        }
        c.close();
        return user;
    }

    public void insertPageData(InstagUser user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(mId, user.getId());
        cv.put(mUseName, user.getName());
        cv.put(mUserAvatar, user.getProfileUrl());
        db.insertOrThrow(mUserTable, null, cv);
        Log.v(TAG, "INSERT USER: " + user.toString());
    }

    public void updatePageData(InstagUser user) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(mId, user.getId());
        cv.put(mUseName, user.getName());
        cv.put(mUserAvatar, user.getProfileUrl());
        int numRowEffect = db.update(mUserTable, cv, mId + "=?",
                new String[]{String.valueOf(user.getId())});
        if (numRowEffect <= 0) {
            insertPageData(user);
            return;
        }
        Log.v(TAG, "UPDATE USER: " + user.toString());
    }


}
