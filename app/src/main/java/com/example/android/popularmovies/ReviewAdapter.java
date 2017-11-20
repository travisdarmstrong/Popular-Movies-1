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

import java.util.ArrayList;

/**
 * Adapter to display user reviews in recyclerview
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyReviewViewHolder> {
    private static final String TAG = "ReviewAdapter";
    private Context context;
    private ArrayList<ReviewData> reviewList;

    public ReviewAdapter(DetailActivity _context, ArrayList<ReviewData> _reviewList){
        context =_context;
        reviewList = _reviewList;
    }

    @Override
    public MyReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.detail_review_item, parent, false);
        return new MyReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.MyReviewViewHolder holder, int position) {
        ReviewData selectedReview = reviewList.get(position);
        holder.reviewAuthor.setText(selectedReview.getAuthor());
        holder.reviewContent.setText(selectedReview.getContent());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView reviewAuthor;
        TextView reviewContent;

        public MyReviewViewHolder(View itemView) {
            super(itemView);
            reviewAuthor = itemView.findViewById(R.id.review_author);
            reviewContent = itemView.findViewById(R.id.review_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            ReviewData selectedReview = reviewList.get(pos);
            Log.i(TAG, "Review item clicked: " + selectedReview.getAuthor());
            String link = selectedReview.getUrl();
            Log.i(TAG, "Opening review at : " + link);

            // Open a new activity to view the video
            Intent watchIntent = new Intent(Intent.ACTION_VIEW);
            watchIntent.setData(Uri.parse(link));
            context.startActivity(watchIntent);
        }
    }
}
