package com.example.android.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.example.android.popularmovies.data.DataContract;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.example.android.popularmovies.utils.Preferences;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String BUNDLE_SAVEDMOVIES = "saved_movies";
    /**
     * This is my api key. For this to work for you, replace this with your own api key
     */
    public static final String apikey = "Your API Key Here";
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
            loadMovieData();
            return true;
        } else if (itemId == R.id.menu_sort_rating) {
            Preferences.setSortOrderTopRated();
            Log.i(TAG, "User selected menu item to Sort By Highest Rating");
            loadMovieData();
            return true;
        }else if (itemId == R.id.menu_sort_favorites) {
            Log.i(TAG, "User selected menu item to only show favorites");
            filterMovieData();
            return true;
        }
        else if (itemId == R.id.menu_refresh) {
            Log.i(TAG, "User requests data refresh");
            loadMovieData();
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
            loadMovieData();
        }
    }

    /**
     * Load {@link MovieData} from network, using a background thread
     */
    private void loadMovieData() {
        Log.d(TAG, "Loading Movie Data");
        setProgressMode();
        new GetMoviesTask().execute();
    }

    private void filterMovieData(){
        Log.d(TAG, "Filtering movie data");
        setProgressMode();
        new FilterMoviesTask().execute();
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

    /**
     * Get movie data from internet using background thread
     */
    private class GetMoviesTask extends AsyncTask<Void, Void, ArrayList<MovieData>> {
        @Override
        protected ArrayList<MovieData> doInBackground(Void... voids) {
            Log.d(TAG, "GetMoviesTask running in background");

            String result = "";
            URL url = NetworkUtils.buildUrl(apikey);

            if (!NetworkUtils.isOnline(MainActivity.this)) {
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
            movieList = result;
            DisplayMovies();
        }
    }

    /**
     * Filter the displayed movies to show only the Favorite movies
     */
    private class FilterMoviesTask extends AsyncTask<Void, Void, ArrayList<MovieData>>{

        @Override
        protected ArrayList<MovieData> doInBackground(Void... voids) {
            Uri uri = DataContract.FavoritesEntry.CONTENT_URI;
            String[] columns = new String[]{DataContract.FavoritesEntry.COLUMN_MOVIE_ID, DataContract.FavoritesEntry.COLUMN_MOVIE_NAME};
            Cursor c = getContentResolver().query(uri, columns, null, null, null);
            ArrayList<Integer> favoriteMovieIds=new ArrayList<>();
            Log.d(TAG, "Got full list of favorite movies");
            if (c!=null && c.moveToFirst()){
                do{
                    int movieId = Integer.valueOf(c.getString(c.getColumnIndex(DataContract.FavoritesEntry.COLUMN_MOVIE_ID)));
                    favoriteMovieIds.add(movieId);
                    String movieName = c.getString(c.getColumnIndex(DataContract.FavoritesEntry.COLUMN_MOVIE_NAME));
                    Log.v(TAG, "Favorite movie (" + movieId + ") - " + movieName);
                }while(c.moveToNext());
            }
            c.close();
            // now filter out the movies that aren't in the favorites list
            ArrayList<MovieData> favMovieList = new ArrayList<>();
            for (MovieData movie: movieList){
                Integer thisId = Integer.valueOf(movie.getId());
                if (favoriteMovieIds.contains(thisId)){
                    favMovieList.add(movie);
                    Log.v(TAG, movie.getTitle() + " is in the favorites list");
                }
            }

            Log.d(TAG, "Movie list now has " + favMovieList.size() + " items in it");
            return favMovieList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "FilterMoviesTask about to run");
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> movieData) {
            super.onPostExecute(movieData);
            Log.d(TAG, "FilterMoviesTask finished");
            movieList = movieData;
            DisplayMovies();
        }
    }
}
