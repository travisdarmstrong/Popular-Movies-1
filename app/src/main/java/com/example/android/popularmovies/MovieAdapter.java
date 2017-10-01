package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;

/**
 * Custom Adapter to display ArrayList of {@link MovieData} in GridView
 */

public class MovieAdapter extends BaseAdapter {
    private static final String TAG = "MovieAdapter";

    private ArrayList<MovieData> movieList;
    private Context context;
    private LayoutInflater inflater;

    public MovieAdapter(MainActivity _mainActivity, ArrayList<MovieData> _movieList) {
        context = _mainActivity;
        movieList = _movieList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int _position) {
        return movieList.get(_position);
    }

    @Override
    public long getItemId(int _position) {
        return _position;
    }

    @Override
    public View getView(final int _position, View view, ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.adapterview, null);
        ImageView imgView = (ImageView) v.findViewById(R.id.img);
        NetworkUtils.loadImage(context, movieList.get(_position).getPosterPath(), imgView);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, String.format("%s clicked", movieList.get(_position).getTitle()));
                Toast.makeText(context, "You clicked " + movieList.get(_position).getTitle(), Toast.LENGTH_SHORT).show();

                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.putExtra(DetailActivity.EXTRA_MOVIE, movieList.get(_position));
                context.startActivity(detailIntent);
            }
        });
        return v;
    }
}
