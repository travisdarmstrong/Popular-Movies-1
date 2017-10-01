package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    public static final String EXTRA_MOVIE = "extra_movie";

    private MovieData movieData;
    @BindView(R.id.detail_image)    ImageView detailPoster;
    @BindView(R.id.detail_title)    TextView detailTitle;
    @BindView(R.id.detail_rating_value)    TextView detailRating;
    @BindView(R.id.detail_releasedate_value)    TextView detailRelease;
    @BindView(R.id.detail_overview)    TextView detailSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Intent callingIntent = getIntent();
        if (callingIntent != null && callingIntent.hasExtra(EXTRA_MOVIE)) {
            Log.v(TAG, "Bundle contains movie data");
            movieData = callingIntent.getParcelableExtra(EXTRA_MOVIE);
            DisplayMovieData();
        }
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
