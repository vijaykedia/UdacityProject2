package com.kediavijay.popularmovies2.listener;

import android.support.v7.widget.RecyclerView;

/**
 * Created by vijaykedia on 14/04/16.
 * This interface can be implement by anyone who want to define the action if item is clicked in
 * {@link android.widget.ListView}/{@link android.widget.GridView}/{@link android.support.v7.widget.RecyclerView}
 */
public interface OnItemClickListener {

    void onItemClick(final RecyclerView.ViewHolder position);
}
