package com.kediavijay.popularmovies2.adapters;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kediavijay.popularmovies2.PopularMoviesApplication;
import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.contentprovider.MovieInfo;
import com.kediavijay.popularmovies2.contentprovider.MovieInfoTable;
import com.kediavijay.popularmovies2.listener.OnItemClickListener;
import com.kediavijay.popularmovies2.util.Util;

/**
 * Created by vijaykedia on 10/04/16.
 * This will serve as adapter for RecyclerView to show movie images on app launch
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieImageViewHolder> {

    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();

    private Cursor cursor;
    private final OnItemClickListener listener;
    private final ImageLoader imageLoader;

    /**
     * Constructor
     *
     * @param cursor   cursor containing data
     * @param listener This will act as a callback when user click on any poster
     */
    public MovieListAdapter(@Nullable final Cursor cursor, @NonNull final OnItemClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
        imageLoader = PopularMoviesApplication.getInstance().getImageLoader();
    }

    @Override
    public MovieImageViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_thumbnail_layout, parent, false);
        return new MovieImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieImageViewHolder holder, final int position) {

        Log.v(LOG_TAG, "onBindViewHolder() -- Binding MovieImageViewHolder to MovieListAdapter");

        cursor.moveToPosition(position);
        final MovieInfo movieInfo = MovieInfoTable.getRow(cursor, false);

        final String url = Util.getTMDBImageUrl(movieInfo.posterPath);

        imageLoader.get(url, ImageLoader.getImageListener(holder.imageView, R.drawable.image_load_placeholder, android.R.drawable.ic_dialog_alert));
        holder.imageView.setImageUrl(url, imageLoader);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull final View v) {
                listener.onItemClick(movieInfo, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    public void swapCursor(@Nullable final Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public static class MovieImageViewHolder extends RecyclerView.ViewHolder {

        private final NetworkImageView imageView;

        public MovieImageViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.movie_list_item_image_view);
        }
    }
}
