package com.kediavijay.popularmovies2.fragments;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.kediavijay.popularmovies2.PopularMoviesApplication;
import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.contentprovider.MovieInfo;
import com.kediavijay.popularmovies2.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vijaykedia on 15/04/16.
 * This will show the movie Summary
 */
public class MovieInfoFragment extends Fragment {

    private static final String LOG_TAG = MovieInfoFragment.class.getSimpleName();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

    private ImageLoader imageLoader;

    Palette.Swatch lightMuted = null;
    Palette.Swatch darkMuted = null;
    Palette.Swatch lightVibrant = null;

    /**
     * Default constructor
     */
    public MovieInfoFragment() {
        imageLoader = PopularMoviesApplication.getInstance().getImageLoader();
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

        final TextView title = (TextView) rootView.findViewById(R.id.movie_info_title_text_view);
        final ImageView posterImage = (ImageView) rootView.findViewById(R.id.movie_info_poster_image_view);
        final TextView releaseDate = (TextView) rootView.findViewById(R.id.movie_info_release_date_text_view);
        final TextView userRating = (TextView) rootView.findViewById(R.id.movie_info_user_rating_text_view);
        final TextView description = (TextView) rootView.findViewById(R.id.movie_info_synopsis_text_view);

        final Bundle bundle = getArguments();
        if (bundle != null) {
            final MovieInfo movieInfo = bundle.getParcelable("movieInfoParcel");
            if (movieInfo != null) {
                title.setText(movieInfo.title);

                imageLoader.get(Util.getTMDBImageUrl(movieInfo.posterPath), ImageLoader.getImageListener(posterImage, R.drawable.image_load_placeholder, android.R.drawable.ic_dialog_alert));

                releaseDate.setText(String.format(Locale.ENGLISH, "%s", dateFormat.format(new Date(movieInfo.releaseDate))));
                userRating.setText(String.format(Locale.ENGLISH, "%.2f",movieInfo.rating));
                description.setText(movieInfo.overview);
            }
        }

        final Drawable drawable = posterImage.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            final Palette palette = new Palette.Builder(((BitmapDrawable)drawable).getBitmap()).generate();
            lightMuted = palette.getLightMutedSwatch();
            darkMuted = palette.getDarkMutedSwatch();
            lightVibrant = palette.getLightVibrantSwatch();


            if (darkMuted != null) {
                title.setBackgroundColor(darkMuted.getRgb());
                if (lightVibrant != null) {
                    title.setTextColor(lightVibrant.getRgb());
                }
            }

            if (lightMuted != null) {
                rootView.setBackgroundColor(lightMuted.getRgb());
            }
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onActivityCreated() -- Host activity onCreate() is completed");

        super.onActivityCreated(savedInstanceState);

        if (lightMuted != null) {
            final AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
            if (appBarLayout != null) {
                appBarLayout.setBackgroundColor(lightMuted.getRgb());
            }
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
}
