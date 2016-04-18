package com.kediavijay.popularmovies2.listener;

import android.support.annotation.NonNull;

import com.kediavijay.popularmovies2.adapters.MovieListAdapter;
import com.kediavijay.popularmovies2.contentprovider.MovieInfo;

/**
 * Created by vijaykedia on 14/04/16.
 * This interface can be implement by anyone who want to define the action if item is clicked in
 * {@link android.widget.ListView}/{@link android.widget.GridView}/{@link android.support.v7.widget.RecyclerView}
 */
public interface OnItemClickListener {

    void onItemClick(@NonNull final MovieInfo movieInfo, final MovieListAdapter.MovieImageViewHolder position);
}
