package com.kediavijay.popularmovies2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kediavijay.popularmovies2.PopularMoviesConstants;
import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.adapters.MovieDetailAdapter;
import com.kediavijay.popularmovies2.contentprovider.MovieInfo;

/**
 * Created by vijaykedia on 15/04/16.
 * This will at as a container fragment which will be used to set up pager view
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private ViewPager viewPager;

    /**
     * Default Constructor
     */
    public MovieDetailFragment() {
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

        // Although rootView above should be PagerView, but call findViewById to avoid any discrepancy
        viewPager = (ViewPager) rootView.findViewById(R.id.movie_detail_pager_view);

        // Set PagerView properties
        setupViewPager();

        final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabbar);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        Log.d(LOG_TAG, "Host activity onCreate is completed");

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {

        Log.d(LOG_TAG, "MovieDetailFragment is visible to user.");

        super.onStart();
    }


    @Override
    public void onResume() {

        Log.d(LOG_TAG, "MovieDetailFragment is visible in running activity.");

        super.onResume();
    }

    @Override
    public void onPause() {

        Log.d(LOG_TAG, "Another activity is in foreground, but the activity in which MovieDetailFragment lives is still visible.");

        super.onPause();
    }


    @Override
    public void onStop() {

        Log.d(LOG_TAG, "MovieDetailFragment is not visible. Either the host activity has been stopped or the fragment has been removed from the activity but added to the back stack.");

        super.onStop();
    }

    @Override
    public void onDestroyView() {

        Log.d(LOG_TAG, "View hierarchy associated with MovieDetailFragment is being removed");

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        Log.d(LOG_TAG, "MovieDetailFragment is no longer in use and is destroyed.");

        super.onDestroy();
    }

    @Override
    public void onDetach() {

        Log.d(LOG_TAG, "MovieDetailFragment is being disassociated from the activity");

        super.onDetach();
    }

    private void setupViewPager() {

        if (viewPager != null) {

            final Bundle receivedBundle = getActivity().getIntent().getExtras();
            final MovieInfo movieInfo;
            if (receivedBundle == null) {
                movieInfo = getArguments().getParcelable("movieInfoParcel");
            } else {
                 movieInfo = receivedBundle.getParcelable("movieInfoParcel");
            }

            if (movieInfo != null) {
                final MovieDetailAdapter adapter = new MovieDetailAdapter(getFragmentManager());

                final MovieInfoFragment movieInfoFragment = new MovieInfoFragment();
                movieInfoFragment.setArguments(receivedBundle);
                adapter.addFragment("Summary", movieInfoFragment);

                final Bundle bundle = new Bundle();
                bundle.putInt(PopularMoviesConstants.MOVIE_ID, movieInfo.movieId);

                final ReviewsFragment reviewsFragment = new ReviewsFragment();
                reviewsFragment.setArguments(bundle);
                adapter.addFragment("User Reviews", reviewsFragment);

                final TrailersFragment trailersFragment = new TrailersFragment();
                trailersFragment.setArguments(bundle);
                adapter.addFragment("Trailers", trailersFragment);

                viewPager.setAdapter(adapter);
            } else {
                Log.e(LOG_TAG, "Failed to pass movieInfo object");
            }
        } else {
            Log.e(LOG_TAG, "What the hack");
        }
    }
}
