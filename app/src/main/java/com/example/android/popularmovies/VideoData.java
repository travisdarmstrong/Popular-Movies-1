package com.example.android.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Data for a video resource from a {@link MovieData} object
 */
public class VideoData implements Parcelable {
    private static final String TAG = "VideoData";

    private final String movieId;
    private final String id;
    private final String key;
    private final String name;
    private final String site;
    private final String type;

    // Values for checking type and site
    private static final String SITE_YOUTUBE = "YouTube";
    private static final String URL_YOUTUBE_BASE = "http://www.youtube.com";
    private static final String URL_YOUTUBE_WATCH = "watch";
    private static final String URL_YOUTUBE_QUERY = "v";
    private static final String TYPE_TRAILER = "Trailer";

    /**
     * Get the ID of the associated movie
     */
    public String getMovieId() {
        return movieId;
    }

    /**
     * Get the ID of the video
     */
    public String getId() {
        return id;
    }

    /**
     * Get the key of the video. This is for building the link to the video file
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the name of the video
     */
    public String getName() {
        return name;
    }

    /**
     * Get the site where the video is hosted (i.e. YouTube)
     */
    public String getSite() {
        return site;
    }

    /**
     * Get the type of video (i.e. Trailer, Clip, Featurette)
     */
    public String getType() {
        return type;
    }

    /**
     * Returns TRUE if the video is of type Trailer
     */
    public boolean isTrailer() {
        return type.equals(TYPE_TRAILER);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,", movieId, id, key, name, site, type);
    }

    /**
     * Get the URL where the video is hosted
     * (Note: Only provides links for videos hosted on YouTube at the moment)
     */
    public URL getVideoURL() {
        URL url = null;
        if (site.equals(SITE_YOUTUBE)) {
            Uri uri = Uri.parse(URL_YOUTUBE_BASE).buildUpon()
                    .appendPath(URL_YOUTUBE_WATCH)
                    .appendQueryParameter(URL_YOUTUBE_QUERY, key)
                    .build();
            try {
                url = new URL(uri.toString());
            } catch (MalformedURLException e) {
                Log.e(TAG, "Malformed URL: " + e.toString());
            }
        }
        return url;
    }

    /**
     * Create a new {@link VideoData} object
     */
    public VideoData(String _movieId, String _id, String _key, String _name, String _site, String _type) {
        movieId = _movieId;
        id = _id;
        key = _key;
        name = _name;
        site = _site;
        type = _type;
    }

    /**
     * Create a new {@link VideoData} object from saved resources
     */
    public VideoData(Parcel bundle) {
        movieId = bundle.readString();
        id = bundle.readString();
        key = bundle.readString();
        name = bundle.readString();
        site = bundle.readString();
        type = bundle.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieId);
        parcel.writeString(id);
        parcel.writeString(key);
        parcel.writeString(name);
        parcel.writeString(site);
        parcel.writeString(type);
    }

    public static final Creator<VideoData> CREATOR = new Creator<VideoData>() {
        @Override
        public VideoData createFromParcel(Parcel in) {
            return new VideoData(in);
        }

        @Override
        public VideoData[] newArray(int size) {
            return new VideoData[size];
        }
    };
}
