package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data for a user review
 */

public class ReviewData implements Parcelable{
    private static final String TAG = "ReviewData";

    private final String id;
    private final String author;
    private final String content;
    private final String url;

    /**
     * Get ID of the user review
     */
    public String getId() {
        return id;
    }

    /**
     * Get Author of the user review
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get the text of the review
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the URL where the review is hosted
     */
    public String getUrl() {
        return url;
    }

    /**
     * Create a new user review
     */
    public ReviewData(String _id, String _author, String _content, String _url) {
        id = _id;
        author = _author;
        content = _content;
        url = _url;
    }

    /**
     * Create a user review from stored data
     */
    public ReviewData(Parcel bundle){
        id = bundle.readString();
        author = bundle.readString();
        content = bundle.readString();
        url = bundle.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
    }

    public static final Creator<ReviewData> CREATOR = new Creator<ReviewData>() {
        @Override
        public ReviewData createFromParcel(Parcel in) {
            return new ReviewData(in);
        }

        @Override
        public ReviewData[] newArray(int size) {
            return new ReviewData[size];
        }
    };

}
