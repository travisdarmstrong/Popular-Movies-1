package com.example.android.popularmovies.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.VideoData;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

/**
 * Get video resources in a background task
 */

public class GetTrailersTask extends AsyncTask<String, Void, ArrayList<VideoData>> {
    private static final String TAG = "GetTrailersTask";
    private TaskUtils.AsyncTaskCompleteListener<ArrayList<VideoData>> listener;

    public GetTrailersTask(TaskUtils.AsyncTaskCompleteListener<ArrayList<VideoData>> _listener){
        this.listener = _listener;
    }

    @Override
    protected ArrayList<VideoData> doInBackground(String... params) {
        Log.v(TAG, "Running in background");
        if (params.length < 1) {
            Log.e(TAG, "No input parameter to GetTrailersTask. Movie ID required");
            return null;
        }
        // Build the URL
        String response = "";
        String movieId = params[0];
        URL url = NetworkUtils.getVideosURL(BuildConfig.API_KEY, movieId);

        // Fetch data from the web site
        try {
            response = NetworkUtils.getResponseFromHttp(url);
        } catch (Exception e) {
            Log.e(TAG, "Error getting video data from network: " + e.toString());
        }
        Log.v(TAG, "Response from http: " + response);

        // Parse the JSON response
        ArrayList<VideoData> results = null;
        try {
            results = JsonUtils.parseJsonVideoData(response);
        } catch (JSONException jsonEx) {
            Log.e(TAG, "Error parsing JSON data", jsonEx);
        }

        // return the results
        return results;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v(TAG, "GetTrailersTask about to run");
    }

    @Override
    protected void onPostExecute(ArrayList<VideoData> videoData) {
        super.onPostExecute(videoData);
        Log.d(TAG, "GetTrailersTask Finished. Found " + videoData.size() + " videos");
        listener.onTaskComplete(videoData);
    }



}
