package com.kediavijay.popularmovies2.adapters;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.contentprovider.ReviewTable;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vijaykedia on 10/04/16.
 * This class will server as adapter class for {@link android.support.v7.widget.RecyclerView} to show movie reviews
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private static final String LOG_TAG = ReviewsAdapter.class.getSimpleName();

    private Cursor cursor;

    /**
     * Default Constructor
     */
    public ReviewsAdapter(@Nullable final Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        Log.i(LOG_TAG, "onCreateViewHolder() -- Creating review item view holder.");

        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_review_item_layout, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewViewHolder holder, final int position) {

        Log.i(LOG_TAG, "onBindViewHolder() -- Updating ReviewViewHolder wih review content.");

        cursor.moveToPosition(position);

        holder.author.setText(cursor.getString(cursor.getColumnIndex(ReviewTable.FIELD_AUTHOR)));
        holder.content.setText(cursor.getString(cursor.getColumnIndex(ReviewTable.FIELD_CONTENT)));
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            cursor.getCount();
        }
        return 0;
    }

    public void swapCursor(@Nullable final Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.review_author)
        public TextView author;
        @Bind(R.id.review_content)
        public TextView content;

        public ReviewViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
