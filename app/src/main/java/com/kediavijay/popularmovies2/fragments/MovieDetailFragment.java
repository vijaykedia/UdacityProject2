package com.kediavijay.popularmovies2.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.kediavijay.popularmovies2.PopularMoviesApplication;
import com.kediavijay.popularmovies2.PopularMoviesConstants;
import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.adapters.MovieDetailAdapter;
import com.kediavijay.popularmovies2.adapters.MovieListAdapter;
import com.kediavijay.popularmovies2.contentprovider.MovieInfoTable;
import com.kediavijay.popularmovies2.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vijaykedia on 15/04/16.
 * This will at as a container fragment which will be used to set up pager view
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    // ------------------------------
    @Bind(R.id.movie_detail_image_toolbar)
    public ImageView toolbarImageView;

    @Bind(R.id.tab_layout)
    public TabLayout tabLayout;

    @Bind(R.id.movie_detail_pager_view)
    public ViewPager pager;

    // ------------------------------
    private static final int LOADER_ID = 50;
    private MovieSummaryLoaderCallbacks loader;

    private ImageLoader imageLoader;

    private Bundle bundle;

    /**
     * Default Constructor
     */
    public MovieDetailFragment() {
        loader = new MovieSummaryLoaderCallbacks();
        imageLoader = PopularMoviesApplication.getInstance().getImageLoader();
    }

    @Override
    public void onAttach(@NonNull final Context context) {

        Log.d(LOG_TAG, "onAttach() -- MovieDetailFragment has been associated with the activity.");

        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreate() -- MovieDetailFragment created.");

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView() -- Creating view hierarchy associated with MovieDetailFragment defined in movie_detail_layout.xml");

        final View rootView = inflater.inflate(R.layout.movie_detail_layout, container, false);
        ButterKnife.bind(this, rootView);

        // Set toolbar properties
        final AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        final ActionBar actionBar = appCompatActivity.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        bundle = getActivity().getIntent().getExtras();
        if (bundle == null) {
            bundle = getArguments();
        }

        // Set PagerView properties
        setupViewPager();

        tabLayout.setupWithViewPager(pager);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onActivityCreated() -- Host activity onCreate is completed");

        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, bundle, loader);
    }

    @Override
    public void onStart() {

        Log.d(LOG_TAG, "onStart() -- MovieDetailFragment is visible to user.");

        super.onStart();
    }


    @Override
    public void onResume() {

        Log.d(LOG_TAG, "onResume() -- MovieDetailFragment is visible in running activity.");

        super.onResume();
    }

    @Override
    public void onPause() {

        Log.d(LOG_TAG, "onPause() -- Another activity is in foreground, but the activity in which MovieDetailFragment lives is still visible.");

        super.onPause();
    }


    @Override
    public void onStop() {

        Log.d(LOG_TAG, "onStop() -- MovieDetailFragment is not visible. Either the host activity has been stopped or the fragment has been removed from the activity but added to the back stack.");

        super.onStop();
    }

    @Override
    public void onDestroyView() {

        Log.d(LOG_TAG, "onDestroyView() -- View hierarchy associated with MovieDetailFragment is being removed");

        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {

        Log.d(LOG_TAG, "onDestroy() -- MovieDetailFragment is no longer in use and is destroyed.");

        super.onDestroy();
    }

    @Override
    public void onDetach() {

        Log.d(LOG_TAG, "onDetach() -- MovieDetailFragment is being disassociated from the activity");

        super.onDetach();
    }

    private void setupViewPager() {

        Log.d(LOG_TAG, "setupViewPager() -- Setting up vew pager");

        final MovieDetailAdapter adapter = new MovieDetailAdapter(getChildFragmentManager());

        MovieInfoFragment movieInfoFragment = new MovieInfoFragment();
        movieInfoFragment.setArguments(bundle);
        adapter.addFragment("Summary", movieInfoFragment);

        ReviewsFragment reviewsFragment = new ReviewsFragment();
        reviewsFragment.setArguments(bundle);
        adapter.addFragment("User Reviews", reviewsFragment);

        TrailersFragment trailersFragment = new TrailersFragment();
        trailersFragment.setArguments(bundle);
        adapter.addFragment("Trailers", trailersFragment);

        pager.setAdapter(adapter);
    }

    /**
     * This class will define the loader callbacks which will be called by {@link LoaderManager} who is responsible
     * for keeping {@link CursorLoader} in line with the lifecycle of {@link com.kediavijay.popularmovies2.activities.MovieDetailActivity}
     * and {@link MovieDetailFragment}
     */
    private class MovieSummaryLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

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

            Log.d(LOG_TAG, "onCreateLoader() -- Creating movie summary cursor loader");

            final Uri uri = MovieInfoTable.CONTENT_URI;
            final int movieId = args.getInt(PopularMoviesConstants.MOVIE_ID);

            return new CursorLoader(getContext(), uri, null, MovieInfoTable.FIELD_MOVIE_ID + " = ?", new String[]{Integer.toString(movieId)}, null);
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

            if (data != null && data.moveToFirst()) {
                imageLoader.get(Util.getTMDBBackdropImageUrl(data.getString(data.getColumnIndex(PopularMoviesConstants.MOVIE_BACKDROP_PATH_KEY))), ImageLoader.getImageListener(toolbarImageView, R.drawable.image_load_placeholder, android.R.drawable.ic_dialog_alert));
            }
        }

        /**
         * Called when previous created loader is being reset
         *
         * @param loader The Loader that is being reset.
         */
        @Override
        public void onLoaderReset(@NonNull final Loader<Cursor> loader) {

            Log.d(LOG_TAG, "onLoaderReset() -- Resetting cursor");
        }
    }
}
