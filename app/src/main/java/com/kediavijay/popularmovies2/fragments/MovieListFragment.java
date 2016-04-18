package com.kediavijay.popularmovies2.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.activities.MovieDetailActivity;
import com.kediavijay.popularmovies2.activities.MovieListActivity;
import com.kediavijay.popularmovies2.adapters.MovieListAdapter;
import com.kediavijay.popularmovies2.contentprovider.MovieInfo;
import com.kediavijay.popularmovies2.contentprovider.MovieInfoTable;
import com.kediavijay.popularmovies2.listener.OnItemClickListener;
import com.kediavijay.popularmovies2.sync.SyncAdapter;
import com.kediavijay.popularmovies2.util.Util;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by vijaykedia on 10/04/16.
 * This will serve the UI for movieList
 */
public class MovieListFragment extends android.support.v4.app.Fragment {

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    // --------------------- Member Variables for CursorLoader ----------------------------
    private static final int LOADER_ID = 10;
    private final MovieLoaderCallbacks loader;

    // --------------------- Member variables for customizing RecyclerView like EndlessScrolling, Adapter
    private RecyclerView recyclerView;
    private final MovieListAdapter movieListAdapter;
    private boolean isLoading = false;

    boolean isTablet;
    int orientation;
    boolean hasTwoPanes;

    private MovieListActivity.ClickListener listener;

    /**
     * Default constructor
     */
    public MovieListFragment() {
        loader = new MovieLoaderCallbacks();
        movieListAdapter = new MovieListAdapter(null, new RecyclerViewItemClickListener());
        listener = new MovieListActivity.ClickListener();
    }

    @Override
    public void onAttach(@NonNull final Context context) {

        Log.d(LOG_TAG, "onAttach() -- MovieListFragment has been associated with the activity.");

        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreate() -- MovieListFragment created.");

        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        Log.i(LOG_TAG, "OnCreateView() -- Creating view hierarchy(Recycler view) associated with MovieListFragment.");

        isTablet = Util.isTablet(getContext());
        orientation = getResources().getConfiguration().orientation;
        hasTwoPanes = getContext().getResources().getBoolean(R.bool.has_two_panes);

        // Inflate recycler_view_layout
        final View rootView = inflater.inflate(R.layout.movie_list_layout, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);


        // Customize recyclerView
        recyclerView.setAdapter(movieListAdapter);

        // Changing the number of columns based on orientation
        int numberOfColumn;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            numberOfColumn = 2;
        } else {
            numberOfColumn = 3;
        }

        // Setting number of columns for tablet based on orientation
        if (isTablet) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                numberOfColumn = 3;
            } else {
                numberOfColumn = 5;
            }
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumn));

        recyclerView.addOnScrollListener(new EndlessScrollListener());
        recyclerView.setItemAnimator(new SlideInLeftAnimator());

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onActivityCreated() -- Host activity onCreate() is completed");

        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, loader);
    }

    @Override
    public void onStart() {

        Log.i(LOG_TAG, "omStart() -- MovieListFragment is visible to user.");

        super.onStart();
    }

    @Override
    public void onResume() {

        Log.i(LOG_TAG, "onResume() -- MovieListFragment is visible in running activity.");

        super.onResume();
    }

    @Override
    public void onPause() {

        Log.i(LOG_TAG, "onPause() -- Another activity is in foreground, but the activity in which MovieListFragment lives is still visible.");

        super.onPause();
    }

    @Override
    public void onStop() {

        Log.i(LOG_TAG, "onStop() -- MovieListFragment is not visible. Either the host activity has been stopped or the fragment has been removed from the activity but added to the back stack.");

        super.onStop();
    }

    @Override
    public void onDestroyView() {

        Log.i(LOG_TAG, "onDestroyView() -- View hierarchy associated with MovieListFragment is being removed");

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        Log.i(LOG_TAG, "onDestroy() -- MovieListFragment is no longer in use and is destroyed.");

        super.onDestroy();
    }

    @Override
    public void onDetach() {

        Log.i(LOG_TAG, "onDetach() -- MovieListFragment is being disassociated from the activity.");

        super.onDetach();
    }

    /**
     * This will implement {@link OnItemClickListener#onItemClick(MovieInfo, MovieListAdapter.MovieImageViewHolder)}, which will handle the scenario when user click on any image in movie list view
     */
    private class RecyclerViewItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(@NonNull final MovieInfo movieInfo, final MovieListAdapter.MovieImageViewHolder holder) {

            Log.i(LOG_TAG, "onItemClick() -- User clinked on image to get more info. Show detail view");

            if (!hasTwoPanes) {
                final Intent detailActivityIntent = new Intent(getContext(), MovieDetailActivity.class);
                detailActivityIntent.putExtra("movieInfoParcel", movieInfo);

                startActivity(detailActivityIntent);
            } else {
                listener.onItemClick(movieInfo, holder);

                ((GridLayoutManager)recyclerView.getLayoutManager()).setSpanCount(2);
                recyclerView.smoothScrollToPosition(holder.getAdapterPosition());
                recyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                final Bundle bundle = new Bundle();
                bundle.putParcelable("movieInfoParcel", movieInfo);
                final MovieDetailFragment detailFragment = new MovieDetailFragment();
                detailFragment.setArguments(bundle);

                final FragmentManager manager = getFragmentManager();
                final FragmentTransaction transaction = manager.beginTransaction();
                try {
                    transaction.replace(R.id.place_holder_fragment_container, detailFragment, "MovieDetailFragment");
                } finally {
                    transaction.commit();
                }
            }
        }
    }

    private class EndlessScrollListener extends RecyclerView.OnScrollListener {

        private final int lastVisibleItemThreshold = 6;

        @Override
        public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {

            Log.d(LOG_TAG, "User scrolled");

            super.onScrolled(recyclerView, dx, dy);
            final int totalItemCount = recyclerView.getLayoutManager().getItemCount();

            final int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

            final String sortOrder = Util.determineSortOrder(getContext());
            final int currentPage = Util.determineCurrentPage(getContext(), sortOrder);
            final int totalPage = Util.determineTotalPage(getContext(), sortOrder);

            if (!isLoading
                    && totalItemCount <= (lastVisibleItem + lastVisibleItemThreshold)
                    && currentPage < totalPage) {

                final Bundle bundle = new Bundle();
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                ContentResolver.requestSync(SyncAdapter.getSyncAccount(getContext()), getContext().getString(R.string.content_authority), bundle);
                isLoading = true;
            }
        }
    }

    /**
     * This class will define the loader callbacks which will be called by {@link LoaderManager} who is responsible
     * for keeping {@link CursorLoader} in line with the lifecycle of {@link com.kediavijay.popularmovies2.activities.MovieListActivity}
     * and {@link MovieListFragment}
     */
    private class MovieLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        /**
         * This method is called whenever {@link LoaderManager#initLoader(int, Bundle, LoaderManager.LoaderCallbacks)} or
         * {@link LoaderManager#restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)} is called.
         *
         * @param id   id which was passed in {@link LoaderManager#initLoader(int, Bundle, LoaderManager.LoaderCallbacks)}
         * @param args Optional args which need to initialize loader
         * @return instance of {@link CursorLoader}
         */
        @Override
        public Loader<Cursor> onCreateLoader(final int id, @Nullable final Bundle args) {

            Log.i(LOG_TAG, "Creating cursor loader");

            final Uri uri = MovieInfoTable.CONTENT_URI;

            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            final String sortOrder = preferences.getString(getContext().getResources().getString(R.string.movie_sort_order_key), null);

            return new CursorLoader(getActivity(), uri, null, null, null, sortOrder + " DESC");
        }

        /**
         * Here we notify {@link MovieListAdapter} to update itself, so that it can populate the imageViews
         *
         * @param loader The Loader that has finished.
         * @param data   The data generated by the Loader.
         */
        @Override
        public void onLoadFinished(@NonNull final Loader<Cursor> loader, @Nullable final Cursor data) {

            Log.i(LOG_TAG, "Loader is finished loading data. Update adapter to refresh UI");

            movieListAdapter.swapCursor(data);
            isLoading = false;
        }

        /**
         * Called when previous created loader is being reset
         *
         * @param loader The Loader that is being reset.
         */
        @Override
        public void onLoaderReset(@NonNull final Loader<Cursor> loader) {

            Log.i(LOG_TAG, "Resetting cursor");

            movieListAdapter.swapCursor(null);
        }
    }
}
