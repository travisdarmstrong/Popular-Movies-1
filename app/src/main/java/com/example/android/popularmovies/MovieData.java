package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Movie Data from themoviedb
 */
public class MovieData implements Parcelable {
    private final static String TAG = "MovieDateClass";
    private final String id;
    private final String title;
    private final String plotSummary;
    private final String userRating;
    private final String releaseDate;
    private final String posterPath;

    /***
     * Movie Data from themoviedb
     */
    public MovieData(String _id, String _title, String _plotSummary, String _userRating, String _releaseDate, String _posterPath) {
        id = _id;
        title = _title;
        plotSummary = _plotSummary;
        userRating = _userRating;
        releaseDate = _releaseDate;
        posterPath = _posterPath;
    }

    /**
     * Movie Data from themoviedb, created by using locally cached data
     */
    private MovieData(Parcel bundle) {
        id = bundle.readString();
        title = bundle.readString();
        plotSummary = bundle.readString();
        userRating = bundle.readString();
        releaseDate = bundle.readString();
        posterPath = bundle.readString();
    }

    /**
     * Get the movie id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the movie title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the movie plot summary
     */
    public String getPlotSummary() {
        return plotSummary;
    }

    /**
     * Get the movie user rating
     */
    public String getUserRating() {
        return userRating;
    }

    /**
     * Get the movie release date
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Get the year of the movie release
     */
    public String getReleaseYear() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
        Date date;
        try {
            date = format.parse(releaseDate);
            return (new SimpleDateFormat("yyyy", Locale.getDefault())).format(date);
        } catch (Exception ex) {
            Log.e(TAG, "Error parsing date: " + ex.toString());
        }
        return "";
    }

    /**
     * Get the path for the movie poster on themoviedb
     */
    public String getPosterPath() {
        return posterPath;
    }

    @Override
    public String toString() {
        return title + "," + plotSummary + "," + userRating + "," + releaseDate + "," + posterPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(plotSummary);
        parcel.writeString(userRating);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}
