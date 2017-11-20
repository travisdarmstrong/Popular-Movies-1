package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Content Provider for Favorites db
 */

public class FavoritesProvider extends ContentProvider {
    private static final String TAG = "FavoritesProvider";

    /**
     * Codes matched to the URI
     */
    public static final int CODE_FAVORITES = 100;
    public static final int CODE_FAVORITES_WITH_MOVIEID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoritesDbHelper dbHelper;

    /**
     * Create a UriMatcher
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher newMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        newMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_FAVORITES, CODE_FAVORITES);
        newMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_FAVORITES + "/#", CODE_FAVORITES_WITH_MOVIEID);
        return newMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new FavoritesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES: {
                cursor = db.query(
                        DataContract.FavoritesEntry.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_FAVORITES_WITH_MOVIEID: {
                String movieId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movieId};
                cursor = db.query(
                        DataContract.FavoritesEntry.TABLE_NAME,
                        columns,
                        DataContract.FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default: {
                Log.e(TAG, "Uri not matched: " + uri);
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("GetType is not implemented yet");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri result;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES: {
                long id = db.insert(
                        DataContract.FavoritesEntry.TABLE_NAME,
                        null,
                        contentValues);
                if (id > 0) {
                    result = ContentUris.withAppendedId(DataContract.FavoritesEntry.CONTENT_URI, id);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return result;
                } else {
                    throw new UnsupportedOperationException("Insert operation failed: " + uri);
                }
            }
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numDeleted;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES: {
                numDeleted = db.delete(
                        DataContract.FavoritesEntry.TABLE_NAME,
                        null,
                        null);
                break;
            }
            case CODE_FAVORITES_WITH_MOVIEID: {
                String movieId = uri.getLastPathSegment();
                String[] args = new String[]{movieId};
                numDeleted = db.delete(
                        DataContract.FavoritesEntry.TABLE_NAME,
                        DataContract.FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                        args);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (numDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Update is not implemented yet");
    }

    @Override
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }
}
