package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;

/**
 * Custom Adapter to display ArrayList of {@link MovieData} in GridView
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {
    private static final String TAG = "MovieAdapter";

    private ArrayList<MovieData> movieList;
    private Context context;

    public MovieAdapter(MainActivity _mainActivity, ArrayList<MovieData> _movieList) {
        context = _mainActivity;
        movieList = _movieList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.adapterview, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageView imgView = holder.imgView;
        NetworkUtils.loadImage(context, movieList.get(position).getPosterPath(), imgView);
    }

    @Override
    public long getItemId(int _position) {
        return _position;
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imgView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgView = (ImageView)itemView.findViewById(R.id.main_item_img);
            imgView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.v(TAG, String.format("%s clicked", movieList.get(position).getTitle()));

            Intent detailIntent = new Intent(context, DetailActivity.class);
            detailIntent.putExtra(DetailActivity.EXTRA_MOVIE, movieList.get(position));
            context.startActivity(detailIntent);
        }
    }
}
