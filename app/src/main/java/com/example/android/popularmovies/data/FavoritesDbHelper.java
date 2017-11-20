package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database Helper
 */

public class FavoritesDbHelper extends SQLiteOpenHelper {
    private static final String TAG="FavoritesDbHelper";

    public static final String DB_NAME = "favorites.db";
    public static final int DB_VERSION = 2;
    private static final String DB_DELETE = "DROP TABLE IF EXISTS ";

    public FavoritesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String DB_CREATE =
                "CREATE TABLE " + DataContract.FavoritesEntry.TABLE_NAME + " (" +
                        DataContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DataContract.FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        DataContract.FavoritesEntry.COLUMN_MOVIE_NAME + " TEXT" +
                        ");";
        sqLiteDatabase.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading table from " + oldVersion + " to " + newVersion);
        sqLiteDatabase.execSQL(DB_DELETE + DataContract.FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
