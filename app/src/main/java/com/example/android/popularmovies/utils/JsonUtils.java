package com.example.android.popularmovies.utils;

import android.util.Log;

import com.example.android.popularmovies.MovieData;
import com.example.android.popularmovies.VideoData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Utilities to parse JSON Object data
 */

public final class JsonUtils {
    private final static String TAG = "JsonUtils";

    /**
     * JSON data for movie listing
     */
    private static final String JSON_RESULTS = "results";
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_POSTERPATH = "poster_path";
    private static final String JSON_PLOT = "overview";
    private static final String JSON_RATING = "vote_average";
    private static final String JSON_RELEASEDATE = "release_date";

    /**
     * JSON data for video resources
     */
    private static final String JSON_VIDEOS_ID = "id";
    private static final String JSON_VIDEOS_KEY = "key";
    private static final String JSON_VIDEOS_NAME = "name";
    private static final String JSON_VIDEOS_SITE = "site";
    private static final String JSON_VIDEOS_TYPE = "type";

    /**
     * Parse the JSON data string into an ArrayList of {@link MovieData}
     */
    public static ArrayList<MovieData> parseJsonData(String jsonString) throws JSONException {
        Log.v(TAG, "Parsing JSON movie data");
        JSONObject rootObject = new JSONObject(jsonString);

        Log.v(TAG, rootObject.toString());

        JSONArray resultsArray = rootObject.getJSONArray(JSON_RESULTS);

        Log.v(TAG, String.format("%s results found", resultsArray.length()));
        ArrayList<MovieData> results = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {

            JSONObject obj = resultsArray.getJSONObject(i);
            String id = obj.getString(JSON_ID);
            String title = obj.getString(JSON_TITLE);
            String posterPath = obj.getString(JSON_POSTERPATH);
            String plotSummary = obj.getString(JSON_PLOT);
            String userRating = obj.getString(JSON_RATING);
            String releaseDate = obj.getString(JSON_RELEASEDATE);

            MovieData newMovie = new MovieData(id, title, plotSummary, userRating, releaseDate, posterPath);
            results.add(newMovie);
            Log.v(TAG, newMovie.toString());
        }
        return results;
    }

    /**
     * Parse the JSON data string into an ArrayList of {@link VideoData}
     */
    public static ArrayList<VideoData> parseJsonVideoData(String jsonString) throws JSONException {
        Log.v(TAG, "Parsing JSON Video data");
        JSONObject rootObject = new JSONObject(jsonString);
        String movieId = rootObject.getString(JSON_ID);
        JSONArray resultsArray = rootObject.getJSONArray(JSON_RESULTS);
        Log.v(TAG, String.format("%s results found", resultsArray.length()));

        ArrayList<VideoData> videoResults = new ArrayList<>();
        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject obj = resultsArray.getJSONObject(i);
            String id = obj.getString(JSON_VIDEOS_ID);
            String key = obj.getString(JSON_VIDEOS_KEY);
            String name = obj.getString(JSON_VIDEOS_NAME);
            String site = obj.getString(JSON_VIDEOS_SITE);
            String type = obj.getString(JSON_VIDEOS_TYPE);
            VideoData newVideo = new VideoData(movieId, id, key, name, site, type);
            // We are only adding Trailers, ignore the rest
            if (newVideo.isTrailer()) {
                videoResults.add(newVideo);
            }
            Log.v(TAG, newVideo.toString());
        }
        return videoResults;
    }
}

