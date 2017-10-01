package com.example.android.popularmovies.utils;

import android.util.Log;

import com.example.android.popularmovies.MovieData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Utilities to parse the JSON Object data
 */

public final class JsonUtils {

    private final static String TAG = "JsonUtils";
    private static final String JSON_RESULTS = "results";
    private static final String JSON_TITLE = "title";
    private static final String JSON_POSTERPATH = "poster_path";
    private static final String JSON_PLOT = "overview";
    private static final String JSON_RATING = "vote_average";
    private static final String JSON_RELEASEDATE = "release_date";

    /**
     * Parse the JSON data string into an ArrayList of {@link MovieData}
     */
    public static ArrayList<MovieData> parseJsonData(String jsonString) throws JSONException {
        Log.v(TAG, "Parsing JSON data");
        JSONObject rootObject = new JSONObject(jsonString);

        Log.v(TAG, rootObject.toString());

        JSONArray resultsArray = rootObject.getJSONArray(JSON_RESULTS);

        Log.v(TAG, String.format("%s results found", resultsArray.length()));
        ArrayList<MovieData> results = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {

            JSONObject obj = resultsArray.getJSONObject(i);

            String title = obj.getString(JSON_TITLE);
            String posterPath = obj.getString(JSON_POSTERPATH);
            String plotSummary = obj.getString(JSON_PLOT);
            String userRating = obj.getString(JSON_RATING);
            String releaseDate = obj.getString(JSON_RELEASEDATE);

            MovieData newMovie = new MovieData(title, plotSummary, userRating, releaseDate, posterPath);
            results.add(newMovie);
            Log.v(TAG, newMovie.toString());
        }
        return results;
    }
}
