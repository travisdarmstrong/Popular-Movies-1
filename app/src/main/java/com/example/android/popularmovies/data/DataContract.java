package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Database Contract for Favorites
 */

public class DataContract {

    /**
     * Content Authority
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    /**
     * Database definition information for Favorites db
     */
    public static final class FavoritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIE_ID = "movieId";

        public static final String COLUMN_MOVIE_NAME = "name";

        public static Uri buildUriForMovie(String movieId){
            return CONTENT_URI.buildUpon()
                    .appendPath(movieId)
                    .build();
        }

    }
}
