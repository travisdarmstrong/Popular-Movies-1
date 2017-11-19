package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

/**
 * Custom Adapter to display list of videos in recyclerview
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyVideoViewHolder> {
    private static final String TAG = "VideoAdapter";
    private Context context;
    private ArrayList<VideoData> videoList;

    /**
     * Create a new {@link VideoAdapter}
     */
    public VideoAdapter(DetailActivity _activity, ArrayList<VideoData> _videoList) {
        context = _activity;
        videoList = _videoList;
    }

    @Override
    public MyVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.detail_video_item, parent, false);
        return new MyVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyVideoViewHolder holder, int position) {
        VideoData video = videoList.get(position);
        holder.videoTitle.setText(video.getName());
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyVideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView videoTitle;

        public MyVideoViewHolder(View itemView) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.video_title);
            //videoTitle.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            VideoData selectedVideo = videoList.get(pos);
            Log.i(TAG, "Video item clicked: " + selectedVideo.getName());
            URL link = selectedVideo.getVideoURL();
            Log.i(TAG, "Opening video at : " + link);

            // Open a new activity to view the video
            Intent watchIntent = new Intent(Intent.ACTION_VIEW);
            watchIntent.setData(Uri.parse(link.toString()));
            context.startActivity(watchIntent);
        }
    }
}
