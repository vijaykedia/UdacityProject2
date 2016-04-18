package com.kediavijay.popularmovies2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

/**
 * Created by vijaykedia on 15/04/16.
 * This will serve the UI for the reviews page
 */
public class ReviewsFragment extends Fragment {

    private static final String LOG_TAG = ReviewsFragment.class.getSimpleName();

    /**
     * Default constructor
     */
    public ReviewsFragment(){
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

        Log.i(LOG_TAG, "onCreateView() -- Creating view hierarchy(Recycler view) associated with ReviewsFragment.");

        final ReviewsAdapter adapter = new ReviewsAdapter();

        final Bundle bundle = getArguments();
        if (bundle != null) {
            final int movieId = bundle.getInt(PopularMoviesConstants.MOVIE_ID);
            adapter.setMovieId(movieId);
        }

        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view_layout, container, false);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return recyclerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onActivityCreated() -- Host activity onCreate() is completed");

        super.onActivityCreated(savedInstanceState);
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
