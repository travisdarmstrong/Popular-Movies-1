package com.example.android.popularmovies.utils;

/**
 * User Preferences
 */

public class Preferences {
    private static final String MOVIES_POPULAR = "popular";
    private static final String MOVIES_TOPRATED = "top_rated";
    private static final String MOVIES_FAVORITES = "favorites";
    private static String SortPreference = MOVIES_POPULAR;

    /**
     * Get preferred sort order
     */
    static public String getSortOrder() {
        return (SortPreference.equals(MOVIES_POPULAR)) ? MOVIES_POPULAR : MOVIES_TOPRATED;
    }

    /**
     * Set sort order preference to Most Popular
     */
    static public void setSortOrderMostPopular() {
        SortPreference = MOVIES_POPULAR;
    }

    /**
     * Set sort order preference to Top Rated
     */
    static public void setSortOrderTopRated() {
        SortPreference = MOVIES_TOPRATED;
    }

}
