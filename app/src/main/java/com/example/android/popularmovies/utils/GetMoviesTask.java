package com.example.android.popularmovies.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.MovieData;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by travis on 11/21/17.
 */

public class GetMoviesTask  extends AsyncTask<Void, Void, ArrayList<MovieData>> {
    private static final String TAG="GetMoviesTask";
    private TaskUtils.AsyncTaskCompleteListener<ArrayList<MovieData>> listener;
    private Context context;

    public GetMoviesTask(Context _context, TaskUtils.AsyncTaskCompleteListener<ArrayList<MovieData>> _listener){
        this.context = _context;
        this.listener = _listener;
    }

    @Override
    protected ArrayList<MovieData> doInBackground(Void... voids) {
        Log.d(TAG, "GetMoviesTask running in background");

        String result = "";
        URL url = NetworkUtils.buildUrl(BuildConfig.API_KEY);

        if (!NetworkUtils.isOnline(context)) {
            Log.e(TAG, "Device is not online!!!");
            return null;
        }
        try {
            result = NetworkUtils.getResponseFromHttp(url);
        } catch (IOException ioEx) {
            Log.e(TAG, "Exception getting data: " + ioEx.toString(), ioEx);
        }
        Log.v(TAG, "Result from Http: " + result);
        if (result.isEmpty()) {
            Log.w(TAG, "Empty response");
            return null;
        }
        // parse the JSON data
        ArrayList<MovieData> movieList = null;
        try {
            movieList = JsonUtils.parseJsonData(result);
        } catch (JSONException jsonEx) {
            Log.e(TAG, "Error parsing JSON data", jsonEx);
        }
        return movieList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "GetMoviesTask about to run");
    }

    @Override
    protected void onPostExecute(ArrayList<MovieData> result) {
        super.onPostExecute(result);
        Log.d(TAG, "GetMoviesTask finished.");
        listener.onTaskComplete(result);
    }
}
