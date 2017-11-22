package com.example.android.popularmovies.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.ReviewData;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

/**
 * Get User Reviews in a background task
 */

public class GetReviewsTask extends AsyncTask<String, Void, ArrayList<ReviewData>> {
    private static final String TAG = "GetReviewsTask";
    private TaskUtils.AsyncTaskCompleteListener<ArrayList<ReviewData>> listener;

    public GetReviewsTask(TaskUtils.AsyncTaskCompleteListener<ArrayList<ReviewData>> _listener){
        this.listener=_listener;
    }

    @Override
    protected ArrayList<ReviewData> doInBackground(String... params) {
        Log.v(TAG, "Running in background");
        if (params.length < 1) {
            Log.e(TAG, "No input parameter to GetReviewsTask. Movie ID required");
            return null;
        }
        // Build the URL
        String response = "";
        String movieId = params[0];
        URL url = NetworkUtils.getReviewsURL(BuildConfig.API_KEY, movieId);

        // Fetch data from the web site
        try {
            response = NetworkUtils.getResponseFromHttp(url);
        } catch (Exception e) {
            Log.e(TAG, "Error getting video data from network: " + e.toString());
        }
        Log.v(TAG, "Response from http: " + response);

        // Parse the JSON response
        ArrayList<ReviewData> results = null;
        try {
            results = JsonUtils.parseJsonReviewData(response);
        } catch (JSONException jsonEx) {
            Log.e(TAG, "Error parsing JSON data", jsonEx);
        }

        // return the results
        return results;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "GetReviewsTask about to run");
    }

    @Override
    protected void onPostExecute(ArrayList<ReviewData> results) {
        super.onPostExecute(results);
        Log.d(TAG, "GetReviewsTask finished. Found " + results.size() + " reviews");
        listener.onTaskComplete(results);
    }

}
