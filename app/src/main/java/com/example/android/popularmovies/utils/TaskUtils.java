package com.example.android.popularmovies.utils;

/**
 * Created by travis on 11/21/17.
 */

public class TaskUtils {
    public interface AsyncTaskCompleteListener<T>{
        public void onTaskComplete(T results);
    }
}
