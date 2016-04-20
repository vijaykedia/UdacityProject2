package com.kediavijay.popularmovies2.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.kediavijay.popularmovies2.PopularMoviesConstants;
import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.activities.MovieDetailActivity;
import com.kediavijay.popularmovies2.adapters.MovieListAdapter;
import com.kediavijay.popularmovies2.contentprovider.MovieInfoTable;
import com.kediavijay.popularmovies2.listener.OnItemClickListener;
import com.kediavijay.popularmovies2.sync.SyncAdapter;
import com.kediavijay.popularmovies2.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vijaykedia on 10/04/16.
 * This will serve the UI for movieList
 */
public class MovieListFragment extends android.support.v4.app.Fragment {

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    // --------------------- View defined in layout --------------------------------------
    @Bind(R.id.spinner)
    public Spinner spinner;
    @Bind(R.id.recycler_view)
    public RecyclerView recyclerView;

    // --------------------- Member Variables for CursorLoader ----------------------------
    private static final int LOADER_ID = 10;
    private final MovieLoaderCallbacks loader;

    // --------------------- Member variables for customizing RecyclerView like EndlessScrolling, Adapter
    private View rootView;
    private final MovieListAdapter movieListAdapter;
    private boolean isLoading = false;

    boolean hasTwoPanes;

    /**
     * Default constructor
     */
    public MovieListFragment() {
        loader = new MovieLoaderCallbacks();
        movieListAdapter = new MovieListAdapter(null, new RecyclerViewItemClickListener());
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

        Log.i(LOG_TAG, "OnCreateView() -- Creating view hierarchy associated with MovieListFragment defined in initial_movie_list_layout.xml");

        // Bind all views
        final View rootView = inflater.inflate(R.layout.initial_screen_layout, container, false);
        ButterKnife.bind(this, rootView);

        hasTwoPanes = getContext().getResources().getBoolean(R.bool.has_two_panes);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new SpinnerOnItemSelectedListener());

        // Customize the number of columns for initial UI which is will show the list of movies
        final int numberOfColumn = getContext().getResources().getInteger(R.integer.initial_screen_column_count);

        // Customize recyclerView
        recyclerView.setAdapter(movieListAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumn));

        recyclerView.addOnScrollListener(new EndlessScrollListener());
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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

        Log.i(LOG_TAG, "onStart() -- MovieListFragment is visible to user.");

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
        ButterKnife.unbind(this);
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
     * This will implement {@link OnItemClickListener#onItemClick(android.support.v7.widget.RecyclerView.ViewHolder)}, which will handle the scenario when user click on any image in movie list view
     */
    private class RecyclerViewItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(final RecyclerView.ViewHolder holder) {

            Log.i(LOG_TAG, "onItemClick() -- User clinked on image to get more info. Show detail view");

            if(hasTwoPanes) {

                // Set RecyclerView properties, so that we can decrease the number of columns
                ((GridLayoutManager)recyclerView.getLayoutManager()).setSpanCount(2);
                recyclerView.setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                recyclerView.getLayoutManager().scrollToPosition(holder.getAdapterPosition());

                final Bundle bundle = new Bundle();
                bundle.putInt(PopularMoviesConstants.MOVIE_ID, ((MovieListAdapter.MovieImageViewHolder) holder).getMovieId());
                final MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(bundle);

                final FragmentManager fragmentManager = getFragmentManager();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                try {
                    transaction.replace(R.id.detail_movie_container, fragment, MovieDetailFragment.class.getSimpleName());
                } finally {
                    transaction.commit();
                }

            } else {
                final Intent detailMovieIntent = new Intent(getContext(), MovieDetailActivity.class);
                detailMovieIntent.putExtra(PopularMoviesConstants.MOVIE_ID, ((MovieListAdapter.MovieImageViewHolder) holder).getMovieId());

                startActivity(detailMovieIntent);
            }
        }
    }

    /**
     * This class implements a listener when user selected a item from spinner
     */
    private class SpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(@NonNull final AdapterView<?> parent, @NonNull final View view, final int position, final long id) {

            Log.d(LOG_TAG, "onItemSelected() -- User changed view selection.");

            final String selection = (String) parent.getItemAtPosition(position);
            if ("Favourites".equals(selection)) {
                // load favourites
                final Bundle bundle = new Bundle();
                getLoaderManager().restartLoader(LOADER_ID, bundle, loader);
            } else {
                final String sortOrder = Util.determineSortOrder(selection);
                Util.setSortOrder(getContext(), sortOrder);
                getLoaderManager().restartLoader(LOADER_ID, null, loader);
            }
        }

        @Override
        public void onNothingSelected(@NonNull final AdapterView<?> parent) {
        }
    }

    /**
     * This class is used to implement endless scrolling
     */
    private class EndlessScrollListener extends RecyclerView.OnScrollListener {

        private final int lastVisibleItemThreshold = 6;

        @Override
        public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {

            Log.v(LOG_TAG, "onScrolled() -- User scrolled");

            super.onScrolled(recyclerView, dx, dy);

            final int totalItemCount = recyclerView.getLayoutManager().getItemCount();
            final int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

            final String sortOrder = Util.getSortOrder(getContext());
            assert sortOrder != null;
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

            Log.d(LOG_TAG, "onCreateLoader() -- Creating movie info cursor loader");

            final Uri uri = MovieInfoTable.CONTENT_URI;

            // If User selected favorites
            if (args != null) {
                return new CursorLoader(getContext(), uri, new String[]{MovieInfoTable.FIELD_MOVIE_ID, MovieInfoTable.FIELD_POSTER_PATH}, "favourite = ?", new String[]{Integer.toString(1)}, null);
            }
            final String sortOrder = Util.getSortOrder(getContext());
            return new CursorLoader(getContext(), uri, new String[]{MovieInfoTable.FIELD_MOVIE_ID, MovieInfoTable.FIELD_POSTER_PATH}, null, null, sortOrder + " DESC");
        }

        /**
         * Here we notify {@link MovieListAdapter} to update itself, so that it can populate the imageViews
         *
         * @param loader The Loader that has finished.
         * @param data   The data generated by the Loader.
         */
        @Override
        public void onLoadFinished(@NonNull final Loader<Cursor> loader, @Nullable final Cursor data) {

            Log.d(LOG_TAG, "onLoadFinished() -- Loader is finished loading data. Update adapter to refresh UI");

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

            Log.d(LOG_TAG, "onLoaderReset() -- Resetting cursor");

            movieListAdapter.swapCursor(null);
        }
    }
}
