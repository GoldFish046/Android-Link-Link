package com.example.work.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LeaderboardContentProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String AUTHORITY = "com.example.work.utils.LeaderboardContentProvider";

    static {
        uriMatcher.addURI(AUTHORITY, "leaderboard", 1);
    }

    private dbConnectHelper dbConnectHelper;


    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context == null) return false;
        dbConnectHelper = new dbConnectHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbConnectHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.query("leaderboard", null, null, null, null, null, "score");
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case 1:
                return "vnd.android.cursor.dir/vnd.com.example.work.utils.LeaderboardContentProvider.leaderboard";
            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbConnectHelper.getWritableDatabase();
        Uri uriReturn;
        long rowId = db.insert("leaderboard", null, values);
        uriReturn = Uri.parse("content://" + AUTHORITY + "/leaderboard/" + rowId);
        getContext().getContentResolver().notifyChange(uri, null);
        return uriReturn;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbConnectHelper.getWritableDatabase();
        int deleteCount;
        deleteCount = db.delete("leaderboard", selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbConnectHelper.getWritableDatabase();
        int updateCount;
        updateCount = db.update("leaderboard", values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }
}
