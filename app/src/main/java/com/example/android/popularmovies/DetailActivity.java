package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.DataContract;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "DetailActivity";
    public static final String EXTRA_MOVIE = "extra_movie";
    private static final String BUNDLE_SAVED_VIDEOS = "saved-videos";
    private static final String BUNDLE_SAVED_REVIEWS = "saved-reviews";
    private static final String BUNDLE_SAVED_FAVORITE = "saved-favorite";

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
    @BindView(R.id.detail_reviews)
    RecyclerView detailReviewsRecyclerView;
    @BindView(R.id.detail_video_label)
    TextView detailVideoLabel;
    @BindView(R.id.detail_review_label)
    TextView detailReviewLabel;

    private ArrayList<VideoData> videoList;
    private boolean isFavorite;
    String[] FAVORITES_PROJECTION = new String[]
            {DataContract.FavoritesEntry._ID,
                    DataContract.FavoritesEntry.COLUMN_MOVIE_ID,
                    DataContract.FavoritesEntry.COLUMN_MOVIE_NAME};
    private static final int LOADER_ID_FAVORITES = 84923;
    private ArrayList<ReviewData> reviewList;

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
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SAVED_REVIEWS)) {
            Log.v(TAG, "Saved instance state has reviews");
            reviewList = savedInstanceState.getParcelableArrayList(BUNDLE_SAVED_REVIEWS);
            DisplayUserReviews();
        } else {
            LoadUserReviews();
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SAVED_FAVORITE)) {
            Log.v(TAG, "Saved instance state has favorite status");
            isFavorite = savedInstanceState.getBoolean(BUNDLE_SAVED_FAVORITE);
            SetFavoriteIcon();
        } else {
            CheckFavoriteStatus();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_detail_share) {
            // Share the first video trailer
            if (videoList.size() > 0) {
                VideoData video = videoList.get(0);
                URL url = video.getVideoURL();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                shareIntent.putExtra(Intent.EXTRA_TEXT, url.toString());
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_subject)));
            } else {
                Toast.makeText(this, getString(R.string.error_share_novideo), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up UI elements
     */
    private void SetupUI() {
        ButterKnife.bind(this);
        LinearLayoutManager videosLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        detailVideoRecyclerView.setLayoutManager(videosLayoutManager);
        detailVideoRecyclerView.setHasFixedSize(true);
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        detailReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        detailReviewsRecyclerView.setHasFixedSize(true);
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
        if (reviewList != null && reviewList.size() > 0) {
            outState.putParcelableArrayList(BUNDLE_SAVED_REVIEWS, reviewList);
        }
        outState.putBoolean(BUNDLE_SAVED_FAVORITE, isFavorite);
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
        if (isFavorite) {
            // remove from favorites
            RemoveFavorite();
        } else {
            // add to favorites
            AddFavorite();
        }
    }

    private void AddFavorite() {
        final Context context = this;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues values = new ContentValues();
                values.put(DataContract.FavoritesEntry.COLUMN_MOVIE_ID, movieData.getId());
                values.put(DataContract.FavoritesEntry.COLUMN_MOVIE_NAME, movieData.getTitle());
                Uri uri = getContentResolver().insert(DataContract.FavoritesEntry.CONTENT_URI, values);
                if (uri != null) {
                    Log.i(TAG, String.format("%s added to favorites list", movieData.getTitle()));
                } else {
                    Log.i(TAG, "Movie was NOT added to favorites list");
                }
                CheckFavoriteStatus();
                return null;
            }

        }.execute();
    }

    private void RemoveFavorite() {
        final Context context = this;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Uri uri = DataContract.FavoritesEntry.buildUriForMovie(movieData.getId());
                int numDeleted = getContentResolver().delete(
                        uri,
                        null,
                        null);
                Log.i(TAG, "" + numDeleted + " items deleted");
                CheckFavoriteStatus();
                return null;
            }
        }.execute();
    }

    /**
     * Check to see if the movie is marked as a Favorite
     */
    private void CheckFavoriteStatus() {
        Log.d(TAG, "Initializing loader to check if movie is a favorite");
        getSupportLoaderManager().initLoader(LOADER_ID_FAVORITES, null, this);
    }

    /**
     * Set the Favorite icon appropriately
     */
    private void SetFavoriteIcon() {
        Log.i(TAG, String.format("%s is %sa favorite", movieData.getTitle(), isFavorite ? "" : "not "));
        if (isFavorite) {
            detailFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            detailFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    /**
     * Load video resources
     */
    private void LoadTrailers() {
        Log.v(TAG, "loading trailers");
        if (!NetworkUtils.isOnline(DetailActivity.this)) {
            Log.e(TAG, "Device is not online");
            Toast.makeText(DetailActivity.this, getString(R.string.error_no_videos), Toast.LENGTH_LONG).show();
        } else {
            new GetTrailersTask().execute(movieData.getId());
        }
    }

    /**
     * Display the video resources
     */
    private void DisplayVideos() {
        Log.v(TAG, "About to display videos");
        if (videoList.size() < 1) {
            Log.e(TAG, "No videos in list");
            detailVideoLabel.setVisibility(View.INVISIBLE);
        } else {
            // set adapter on recyclerview
            detailVideoLabel.setVisibility(View.VISIBLE);
            detailVideoRecyclerView.setAdapter(new VideoAdapter(this, videoList));
        }
    }

    private void LoadUserReviews() {
        Log.d(TAG, "Loading reviews");
        if (!NetworkUtils.isOnline(DetailActivity.this)) {
            Log.e(TAG, "Device is not online");
            Toast.makeText(DetailActivity.this, getString(R.string.error_no_videos), Toast.LENGTH_LONG).show();
        } else {
            new GetReviewsTask().execute(movieData.getId());
        }
    }

    private void DisplayUserReviews() {
        Log.v(TAG, "About to display user reviews");
        if (reviewList.size() < 1) {
            Log.e(TAG, "No reviews in list");
            detailReviewLabel.setVisibility(View.INVISIBLE);
        } else {
            // set adapter on recyclerview
            detailReviewLabel.setVisibility(View.VISIBLE);
            detailReviewsRecyclerView.setAdapter(new ReviewAdapter(this, reviewList));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Creating loader");
        if (id == LOADER_ID_FAVORITES) {
            return new CursorLoader(
                    this,
                    DataContract.FavoritesEntry.buildUriForMovie(movieData.getId()),
                    FAVORITES_PROJECTION,
                    null,
                    null,
                    null);
        }
        Log.e(TAG, "Loader not implemented: " + id);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished: " + loader.getId());
        if (data != null) {
            isFavorite = data.getCount() > 0;
        }
        SetFavoriteIcon();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Loader reset");
    }

    /**
     * Load video resources in background task
     */
    private class GetTrailersTask extends AsyncTask<String, Void, ArrayList<VideoData>> {
        private static final String TAG = "GetTrailersTask";

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
            URL url = NetworkUtils.getVideosURL(MainActivity.apikey, movieId);

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
            videoList = videoData;
            DisplayVideos();
        }
    }

    private class GetReviewsTask extends AsyncTask<String, Void, ArrayList<ReviewData>> {
        private static final String TAG = "GetReviewsTask";

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
            URL url = NetworkUtils.getReviewsURL(MainActivity.apikey, movieId);

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
            reviewList = results;
            DisplayUserReviews();
        }
    }


}
