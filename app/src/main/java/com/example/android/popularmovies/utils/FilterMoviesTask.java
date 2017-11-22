package com.example.android.popularmovies.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.MovieData;
import com.example.android.popularmovies.data.DataContract;

import java.util.ArrayList;

/**
 * Created by travis on 11/21/17.
 */

public class FilterMoviesTask extends AsyncTask<ArrayList<MovieData>, Void, ArrayList<MovieData>> {
    private static final String TAG="FilterMoviesTask";
    private TaskUtils.AsyncTaskCompleteListener<ArrayList<MovieData>> listener;
    private Context context;

    public FilterMoviesTask(Context _context, TaskUtils.AsyncTaskCompleteListener<ArrayList<MovieData>> _listener){
        this.listener=_listener;
        this.context=_context;
    }
    @Override
    protected ArrayList<MovieData> doInBackground(ArrayList<MovieData>[] _movies) {
        Uri uri = DataContract.FavoritesEntry.CONTENT_URI;
        ArrayList<MovieData> movieList = _movies[0];
        String[] columns = new String[]{DataContract.FavoritesEntry.COLUMN_MOVIE_ID, DataContract.FavoritesEntry.COLUMN_MOVIE_NAME};
        Cursor c = context.getContentResolver().query(uri, columns, null, null, null);
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
        listener.onTaskComplete(movieData);
    }
}
