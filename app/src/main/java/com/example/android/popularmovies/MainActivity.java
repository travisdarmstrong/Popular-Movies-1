package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.utils.FilterMoviesTask;
import com.example.android.popularmovies.utils.GetMoviesTask;
import com.example.android.popularmovies.utils.Preferences;
import com.example.android.popularmovies.utils.TaskUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String BUNDLE_SAVEDMOVIES = "saved_movies";
    private ArrayList<MovieData> movieList;
    @BindView(R.id.main_recyclerview) RecyclerView recyclerView;
    @BindView(R.id.main_progressbar) ProgressBar progressBar;
    @BindView(R.id.main_error) TextView errorMessage;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_sort_mostpopular) {
            Log.i(TAG, "User selected menu item to Sort By Most Popular");
            Preferences.setSortOrderMostPopular();
            LoadMovieData();
            return true;
        } else if (itemId == R.id.menu_sort_rating) {
            Preferences.setSortOrderTopRated();
            Log.i(TAG, "User selected menu item to Sort By Highest Rating");
            LoadMovieData();
            return true;
        }else if (itemId == R.id.menu_sort_favorites) {
            Log.i(TAG, "User selected menu item to only show favorites");
            FilterMovieData();
            return true;
        }
        else if (itemId == R.id.menu_refresh) {
            Log.i(TAG, "User requests data refresh");
            LoadMovieData();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Save movie data to local cache
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (movieList != null && movieList.size() > 0) {
            outState.putParcelableArrayList(BUNDLE_SAVEDMOVIES, movieList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setupUI();
        // Check for locally cached movie data, and use it
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SAVEDMOVIES)) {
            Log.d(TAG, "Saved instance state has saved movie data");
            ArrayList<MovieData> savedMovies = savedInstanceState.getParcelableArrayList(BUNDLE_SAVEDMOVIES);
            Log.d(TAG, "Found " + savedMovies.size() + " movies");
            movieList = savedMovies;
            DisplayMovies();
        } else {
            LoadMovieData();
        }
    }

    /**
     * Load {@link MovieData} from network, using a background thread
     */
    private void LoadMovieData() {
        Log.d(TAG, "Loading Movie Data");
        setProgressMode();
        new GetMoviesTask(this, new TaskUtils.AsyncTaskCompleteListener<ArrayList<MovieData>>() {
            @Override
            public void onTaskComplete(ArrayList<MovieData> results) {
                movieList = results;
                DisplayMovies();
            }
        }).execute();
    }

    /**
     * Filter movies for only favorites
     */
    private void FilterMovieData(){
        Log.d(TAG, "Filtering movie data");
        setProgressMode();
        new FilterMoviesTask(this, new TaskUtils.AsyncTaskCompleteListener<ArrayList<MovieData>>() {
            @Override
            public void onTaskComplete(ArrayList<MovieData> results) {
                movieList = results;
                DisplayMovies();
            }
        }).execute(movieList);
    }


    /**
     * Connect the UI elements
     */
    private void setupUI() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.num_columns));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    /**
     * Show the Progress Bar
     */
    private void setProgressMode() {
        errorMessage.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the progress bar, show movie data
     */
    private void setDataMode() {
        errorMessage.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Show error message
     */
    private void setErrorMode() {
        errorMessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Display the movie data
     */
    private void DisplayMovies() {
        Log.d(TAG, "About to display movies");
        setDataMode();
        if (movieList == null || movieList.size() == 0) {
            Log.e(TAG, "Movie list is empty!!!");
            setErrorMode();
        } else {
            recyclerView.setAdapter(new MovieAdapter(MainActivity.this, movieList));
        }
    }


}
