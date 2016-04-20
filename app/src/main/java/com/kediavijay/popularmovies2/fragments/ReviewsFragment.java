package com.kediavijay.popularmovies2.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kediavijay.popularmovies2.PopularMoviesConstants;
import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.adapters.ReviewsAdapter;
import com.kediavijay.popularmovies2.contentprovider.ReviewTable;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vijaykedia on 15/04/16.
 * This will serve the UI for the reviews page
 */
public class ReviewsFragment extends Fragment {

    private static final String LOG_TAG = ReviewsFragment.class.getSimpleName();

    // --------------------- View defined in layout --------------------------------------
    @Bind(R.id.recycler_view)
    public RecyclerView recyclerView;

    // --------------------- member variables for adapter and loader ---------------------
    private static final int LOADER_ID = 20;

    private final ReviewsAdapter adapter;
    private final ReviewLoaderCallbacks loader;

    /**
     * Default constructor
     */
    public ReviewsFragment() {
        adapter = new ReviewsAdapter(null);
        loader = new ReviewLoaderCallbacks();
    }

    @Override
    public void onAttach(@NonNull final Context context) {

        Log.i(LOG_TAG, "onAttach() -- ReviewsFragment has been associated with the activity.");

        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onCreate() -- ReviewsFragment created.");

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onCreateView() -- Creating view hierarchy associated with ReviewsFragment defined in recycler_view_layout.xml.");

        final View rootView = inflater.inflate(R.layout.recycler_view_layout, container, false);
        ButterKnife.bind(this, rootView);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onActivityCreated() -- Host activity onCreate() is completed");

        super.onActivityCreated(savedInstanceState);

        final Bundle bundle = getArguments();
        if (bundle != null) {
            getLoaderManager().initLoader(LOADER_ID, bundle, loader);
        }
    }

    @Override
    public void onStart() {

        Log.i(LOG_TAG, "onStart() -- ReviewsFragment is visible to user.");

        super.onStart();
    }

    @Override
    public void onResume() {

        Log.i(LOG_TAG, "onResume() -- ReviewsFragment is visible in running activity.");

        super.onResume();
    }

    @Override
    public void onPause() {

        Log.i(LOG_TAG, "onPause() -- Another activity is in foreground, but the activity in which ReviewsFragment lives is still visible.");

        super.onPause();
    }

    @Override
    public void onStop() {

        Log.i(LOG_TAG, "onStop() -- ReviewsFragment is not visible. Either the host activity has been stopped or the fragment has been removed from the activity but added to the back stack.");

        super.onStop();
    }

    @Override
    public void onDestroyView() {

        Log.i(LOG_TAG, "onDestroyView() -- View hierarchy associated with ReviewsFragment is being removed");

        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {

        Log.i(LOG_TAG, "onDestroy() -- ReviewsFragment is no longer in use and is destroyed.");

        super.onDestroy();
    }

    @Override
    public void onDetach() {

        Log.i(LOG_TAG, "onDetach() -- ReviewsFragment is being disassociated from the activity");

        super.onDetach();
    }

    /**
     * This class will define the loader callbacks which will be called by {@link LoaderManager} who is responsible
     * for keeping {@link CursorLoader} in line with the lifecycle of hosting activity and {@link ReviewsFragment}
     */
    private class ReviewLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        /**
         * This method is called whenever {@link LoaderManager#initLoader(int, Bundle, LoaderManager.LoaderCallbacks)} or
         * {@link LoaderManager#restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)} is called.
         *
         * @param id   id which was passed in {@link LoaderManager#initLoader(int, Bundle, LoaderManager.LoaderCallbacks)}
         * @param args Optional args which need to initialize loader
         * @return instance of {@link CursorLoader}
         */
        @Override
        public Loader<Cursor> onCreateLoader(final int id, @NonNull final Bundle args) {

            Log.d(LOG_TAG, "onCreateLoader() -- Creating reviews cursor loader");

            final Uri uri = ReviewTable.CONTENT_URI;

            final int movieId = args.getInt(PopularMoviesConstants.MOVIE_ID);
            return new CursorLoader(getContext(), uri, new String[]{ReviewTable.FIELD_AUTHOR, ReviewTable.FIELD_CONTENT}, ReviewTable.FIELD_MOVIE_ID + " = ?", new String[]{Integer.toString(movieId)}, null);
        }

        /**
         * Here we notify {@link ReviewsAdapter} to update itself, so that it can populate the content
         *
         * @param loader The Loader that has finished.
         * @param data   The data generated by the Loader.
         */
        @Override
        public void onLoadFinished(@NonNull final Loader<Cursor> loader, @Nullable final Cursor data) {

            Log.d(LOG_TAG, "onLoadFinished() -- Loader is finished loading data. Update adapter to refresh reviews UI");

            adapter.swapCursor(data);
        }

        /**
         * Called when previous created loader is being reset
         *
         * @param loader The Loader that is being reset.
         */
        @Override
        public void onLoaderReset(@NonNull final Loader<Cursor> loader) {

            Log.d(LOG_TAG, "onLoaderReset() -- Resetting cursor");

            adapter.swapCursor(null);
        }
    }
}
