package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.utils.ApiKey;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    public static final String EXTRA_MOVIE = "extra_movie";
    private static final String BUNDLE_SAVED_VIDEOS = "saved-videos";

    private MovieData movieData;
    @BindView(R.id.detail_image)
    ImageView detailPoster;
    @BindView(R.id.detail_title)
    TextView detailTitle;
    @BindView(R.id.detail_rating_value)
    TextView detailRating;
    @BindView(R.id.detail_release_value)
    TextView detailRelease;
    @BindView(R.id.detail_overview)
    TextView detailSummary;
    @BindView(R.id.detail_favorite)
    ImageView detailFavorite;
    @BindView(R.id.detail_videos)
    RecyclerView detailVideoRecyclerView;
    private ArrayList<VideoData> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        SetupUI();
        Intent callingIntent = getIntent();
        if (callingIntent != null && callingIntent.hasExtra(EXTRA_MOVIE)) {
            Log.v(TAG, "Bundle contains movie data");
            movieData = callingIntent.getParcelableExtra(EXTRA_MOVIE);
            DisplayMovieData();
        }
        // See if we have locally cached data
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SAVED_VIDEOS)) {
            Log.v(TAG, "Saved instance state has data");
            videoList = savedInstanceState.getParcelableArrayList(BUNDLE_SAVED_VIDEOS);
            DisplayVideos();
        } else {
            LoadTrailers();
        }
    }

    /**
     * Set up UI elements
     */
    private void SetupUI() {
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        detailVideoRecyclerView.setLayoutManager(layoutManager);
        detailVideoRecyclerView.setHasFixedSize(true);
    }

    /**
     * Save video list to local cache
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (videoList != null && videoList.size() > 0) {
            outState.putParcelableArrayList(BUNDLE_SAVED_VIDEOS, videoList);
        }
    }

    /**
     * Display the movie data on the screen
     */
    private void DisplayMovieData() {
        NetworkUtils.loadImage(getApplicationContext(), movieData.getPosterPath(), detailPoster);
        detailTitle.setText(movieData.getTitle());
        detailRating.setText(getString(R.string.rating_denominator, movieData.getUserRating()));
        detailRelease.setText(movieData.getReleaseYear());
        detailSummary.setText(movieData.getPlotSummary());
    }

    /**
     * Handle click event on the Favorite icon
     */
    public void onFavoriteClick(View view) {
        Log.v(TAG, "Favorite clicked");
        String addOrRemove = " added to ";
        String favMsg = movieData.getTitle() + addOrRemove + "favorites list";
        Toast.makeText(this, favMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Load video resources
     */
    private void LoadTrailers() {
        Log.v(TAG, "loading trailers");
        new GetTrailersTask().execute(movieData.getId());
    }

    /**
     * Display the video resources
     */
    private void DisplayVideos() {
        Log.v(TAG, "About to display videos");
        if (videoList.size() < 1) {
            Log.e(TAG, "No videos in list");
        } else {
            // set adapter on recyclerview
            detailVideoRecyclerView.setAdapter(new VideoAdapter(this, videoList));
        }
    }

    /**
     * Load video resources in background task
     */
    private class GetTrailersTask extends AsyncTask<String, Void, ArrayList<VideoData>> {
        private static final String TAG = "GetTrailersTask";

        @Override
        protected ArrayList<VideoData> doInBackground(String... params) {
            Log.v(TAG, "Running in background");
            if (!NetworkUtils.isOnline(DetailActivity.this)) {
                Log.e(TAG, "Device is not online");
                Toast.makeText(DetailActivity.this, getString(R.string.error_no_videos), Toast.LENGTH_LONG).show();
                return null;
            }
            if (params.length < 1) {
                Log.e(TAG, "No input parameter to GetTrailersTask. Movie ID required");
                return null;
            }

            // Build the URL
            String response = "";
            String movieId = params[0];
            URL url = NetworkUtils.getVideosURL(ApiKey.API_KEY, movieId);

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
            Log.v(TAG, "GetTrailersTask Finished. Found " + videoData.size() + " videos");
            videoList = videoData;
            DisplayVideos();
        }
    }
}
