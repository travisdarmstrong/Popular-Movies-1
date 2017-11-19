package com.example.android.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Utilities for network activity
 */

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    private static final String IMAGE_URL_BASE = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_URL_SIZE = "w185/";

    private static final String MOVIEDB_BASE = "http://api.themoviedb.org/3/movie/";
    private static final String QUERY_APIKEY = "api_key";

    private static final String MOVIEDB_VIDEOS = "videos";

    /**
     * Build the URL for querying movies
     * Based on user preferences, it will sort by Most Popular or Highest Rated
     */
    public static URL buildUrl(String apikey) {
        URL url = null;
        Uri uri = Uri.parse(MOVIEDB_BASE).buildUpon()
                .appendPath(Preferences.getSortOrder())
                .appendQueryParameter(QUERY_APIKEY, apikey)
                .build();
        try {
            url = new URL(uri.toString());
        } catch
                (MalformedURLException malEx) {
            Log.e(TAG, "Bad URL", malEx);
        } catch (Exception ex) {
            Log.e(TAG, "Error generating URL", ex);
        }
        if (url != null) {
            Log.v(TAG, "Built URL: " + url.toString());
        }
        return url;
    }

    /**
     * Get response from URL
     */
    public static String getResponseFromHttp(URL url) throws IOException {
        String result = "";
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException ioEx) {
            Log.e(TAG, "IOException getting data: " + ioEx.toString(), ioEx);
            throw ioEx;
        } catch (Exception ex) {
            Log.e(TAG, "Exception getting data: " + ex.toString(), ex);
        }
        Log.v(TAG, "Received response from Http: " + result);
        return result;
    }

    /**
     * Is the system online
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Load image given the poster path, and put it into an imageview
     */
    public static void loadImage(Context context, String _posterPath, ImageView _imgView) {
        String url = NetworkUtils.IMAGE_URL_BASE + NetworkUtils.IMAGE_URL_SIZE + _posterPath;
        try {
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.ic_cloud_download_black_24dp)
                    .error(R.drawable.notfound)
                    .into(_imgView);
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.toString(), e);
            _imgView.setImageResource(R.drawable.notfound);
        }
    }

    /**
     * Get the URL for the video resources
     */
    public static URL getVideosURL(String apikey, String movieId) {
        URL url = null;
        Uri uri = Uri.parse(MOVIEDB_BASE).buildUpon()
                .appendPath(movieId).appendPath(MOVIEDB_VIDEOS)
                .appendQueryParameter(QUERY_APIKEY, apikey)
                .build();
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException malEx) {
            Log.e(TAG, "Bad URL", malEx);
        } catch (Exception ex) {
            Log.e(TAG, "Error generating URL", ex);
        }
        if (url != null) {
            Log.i(TAG, "Videos URL: " + url);
        }
        return url;
    }

}




















