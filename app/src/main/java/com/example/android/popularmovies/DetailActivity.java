package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    public static final String EXTRA_MOVIE = "extra_movie";
    private MovieData movieData;
    private ImageView detailPoster;
    private TextView detailTitle;
    private TextView detailRating;
    private TextView detailRelease;
    private TextView detailSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setupUI();
        Intent callingIntent = getIntent();
        if (callingIntent != null && callingIntent.hasExtra(EXTRA_MOVIE)) {
            Log.v(TAG, "Bundle contains movie data");
            movieData = callingIntent.getParcelableExtra(EXTRA_MOVIE);
            DisplayMovieData();
        }
    }

    /**
     * Connect the UI elements
     */
    private void setupUI() {
        detailPoster = (ImageView) findViewById(R.id.detail_image);
        detailTitle = (TextView) findViewById(R.id.detail_title);
        detailRating = (TextView) findViewById(R.id.detail_rating_value);
        detailRelease = (TextView) findViewById(R.id.detail_releasedate_value);
        detailSummary = (TextView) findViewById(R.id.detail_overview);
    }

    /**
     * Display the movie data on the screen
     */
    private void DisplayMovieData() {
        NetworkUtils.loadImage(getApplicationContext(), movieData.getPosterPath(), detailPoster);
        detailTitle.setText(movieData.getTitle());
        detailRating.setText(movieData.getUserRating());
        detailRelease.setText(movieData.getReleaseDate());
        detailSummary.setText(movieData.getPlotSummary());
    }
}
