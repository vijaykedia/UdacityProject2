package com.kediavijay.popularmovies2.fragments;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.kediavijay.popularmovies2.PopularMoviesApplication;
import com.kediavijay.popularmovies2.PopularMoviesConstants;
import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.adapters.MovieListAdapter;
import com.kediavijay.popularmovies2.contentprovider.MovieInfoTable;
import com.kediavijay.popularmovies2.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vijaykedia on 15/04/16.
 * This will show the movie Summary
 */
public class MovieInfoFragment extends Fragment {

    private static final String LOG_TAG = MovieInfoFragment.class.getSimpleName();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

    // ------------------------------------ Defining views used in layout --------------------------------
    @Bind(R.id.summary_body)
    public ScrollView body;

    @Bind(R.id.movie_info_title_text_view)
    public TextView title;

    @Bind(R.id.movie_info_poster_image_view)
    public ImageView posterImage;

    @Bind(R.id.movie_info_release_date_text_view)
    public TextView releaseDate;

    @Bind(R.id.movie_info_user_rating_text_view)
    public TextView userRating;

    @Bind(R.id.movie_info_synopsis_text_view)
    public TextView description;

    // ----------------------------------- Member variables
    private static final int LOADER_ID = 40;
    private final ImageLoader imageLoader;
    private final MovieInfoLoaderCallbacks loader;

    Palette.Swatch muted = null;
    Palette.Swatch lightMuted = null;
    Palette.Swatch darkMuted = null;

    /**
     * Default constructor
     */
    public MovieInfoFragment() {
        imageLoader = PopularMoviesApplication.getInstance().getImageLoader();
        loader = new MovieInfoLoaderCallbacks();
    }

    @Override
    public void onAttach(@NonNull final Context context) {

        Log.i(LOG_TAG, "onAttach() -- MovieInfoFragment has been associated with the activity.");

        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onCreate() -- MovieInfoFragment created.");

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onCreateView() -- Creating view hierarchy associated with MovieInfoFragment defined in movie_summary_layout.xml");

        final View rootView = inflater.inflate(R.layout.movie_summary_layout, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onActivityCreated() -- Host activity onCreate() is completed");

        super.onActivityCreated(savedInstanceState);

        final Bundle bundle = getArguments();
        getLoaderManager().initLoader(LOADER_ID, bundle, loader);
    }

    @Override
    public void onStart() {

        Log.i(LOG_TAG, "onStart() -- ReviewsFragment is visible to user.");

        super.onStart();
    }

    @Override
    public void onResume() {

        Log.i(LOG_TAG, "onResume() -- MovieInfoFragment is visible in running activity.");

        super.onResume();
    }

    @Override
    public void onPause() {

        Log.i(LOG_TAG, "onPause() -- Another activity is in foreground, but the activity in which MovieInfoFragment lives is still visible.");

        super.onPause();
    }

    @Override
    public void onStop() {

        Log.i(LOG_TAG, "onStop() -- MovieInfoFragment is not visible. Either the host activity has been stopped or the fragment has been removed from the activity but added to the back stack.");

        super.onStop();
    }

    @Override
    public void onDestroyView() {

        Log.i(LOG_TAG, "onDestroyView() -- View hierarchy associated with MovieInfoFragment is being removed");

        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {

        Log.i(LOG_TAG, "onDestroy() -- MovieInfoFragment is no longer in use and is destroyed.");

        super.onDestroy();
    }

    @Override
    public void onDetach() {

        Log.i(LOG_TAG, "onDetach() -- MovieInfoFragment is being disassociated from the activity");

        super.onDetach();
    }

    private void updateViews(@NonNull final Cursor cursor) {

        Log.d(LOG_TAG, "updateViews() -- Updating view with new data");

        if (cursor.moveToFirst()) {

            title.setText(cursor.getString(cursor.getColumnIndex(PopularMoviesConstants.MOVIE_TITLE_KEY)));

            imageLoader.get(Util.getTMDBPosterImageUrl(cursor.getString(cursor.getColumnIndex(PopularMoviesConstants.MOVIE_POSTER_PATH_KEY))), ImageLoader.getImageListener(posterImage, R.drawable.image_load_placeholder, android.R.drawable.ic_dialog_alert));

            releaseDate.setText(String.format(Locale.ENGLISH, "%s", dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(PopularMoviesConstants.MOVIE_RELEASE_DATE_KEY))))));
            userRating.setText(String.format(Locale.ENGLISH, "%.2f", cursor.getDouble(cursor.getColumnIndex(PopularMoviesConstants.MOVIE_USER_RATING_KEY))));
            description.setText(cursor.getString(cursor.getColumnIndex(PopularMoviesConstants.MOVIE_OVERVIEW_KEY)));

            final Drawable drawable = posterImage.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                final Palette palette = new Palette.Builder(((BitmapDrawable) drawable).getBitmap()).generate();
                muted = palette.getMutedSwatch();
                lightMuted = palette.getLightMutedSwatch();
                darkMuted = palette.getDarkMutedSwatch();

                if (muted != null) {
                    body.setBackgroundColor(muted.getRgb());
                    title.setTextColor(muted.getTitleTextColor());
                    description.setTextColor(muted.getBodyTextColor());
                } else if (lightMuted != null) {
                    body.setBackgroundColor(lightMuted.getRgb());
                    title.setTextColor(lightMuted.getTitleTextColor());
                    description.setTextColor(lightMuted.getBodyTextColor());
                } else if (darkMuted != null) {
                    body.setBackgroundColor(darkMuted.getRgb());
                    title.setTextColor(darkMuted.getTitleTextColor());
                    description.setTextColor(darkMuted.getBodyTextColor());
                }
            }
        }
    }

    /**
     * This class will define the loader callbacks which will be called by {@link LoaderManager} who is responsible
     * for keeping {@link CursorLoader} in line with the lifecycle of hosting activity and {@link MovieInfoFragment}
     */
    private class MovieInfoLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

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

            Log.d(LOG_TAG, "onCreateLoader() -- Creating movie info cursor loader");


            final int movieId = args.getInt(PopularMoviesConstants.MOVIE_ID);
            final Uri uri = ContentUris.withAppendedId(MovieInfoTable.CONTENT_URI, movieId);

            return new CursorLoader(getContext(), uri, null, null, null, null);
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
            if (data != null) {
                updateViews(data);
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
