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
import com.kediavijay.popularmovies2.contentprovider.TrailerTable;
import com.kediavijay.popularmovies2.listener.OnItemClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vijaykedia on 10/04/16.
 * This class will server as adapter class for {@link android.support.v7.widget.RecyclerView} to show movie trailers/videos
 */
public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private static final String LOG_TAG = TrailersAdapter.class.getSimpleName();

    private final OnItemClickListener listener;
    private Cursor cursor;

    /**
     * Default Constructor
     */
    public TrailersAdapter(@Nullable final Cursor cursor, @NonNull final OnItemClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        Log.i(LOG_TAG, "onCreateViewHolder() -- Creating trailer item view holder.");

        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_trailer_item_layout, parent, false);
        return new TrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TrailerViewHolder holder, final int position) {

        Log.i(LOG_TAG, "onBindViewHolder() -- Updating TrailerViewHolder wih trailers.");

        cursor.moveToPosition(position);
        holder.bind(holder, cursor);
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    public void swapCursor(@Nullable final Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.movie_trailer_name) public TextView trailerName;

        private String key;

        public TrailerViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull final TrailerViewHolder holder, @NonNull final Cursor cursor) {

            trailerName.setText(cursor.getString(cursor.getColumnIndex(TrailerTable.FIELD_NAME)));
            key = cursor.getString(cursor.getColumnIndex(TrailerTable.FIELD_KEY));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull final View v) {
                    listener.onItemClick(holder);
                }
            });
        }

        public String getKey() {
            return key;
        }
    }
}
