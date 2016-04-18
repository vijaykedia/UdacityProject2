package com.kediavijay.popularmovies2.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.fragments.MovieDetailFragment;

/**
 * Created by vijaykedia on 10/04/16.
 * This will define the activity which will show users the detailed movie info, trailers and reviews
 */
public class MovieDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreate() -- MovieDetailActivity is being created.");

        super.onCreate(savedInstanceState);

        // Set Content View
        setContentView(R.layout.general_activity_layout);

        // Set Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(4);
            actionBar.setTitle("Movie Detail");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Add fragment
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        try {
            transaction.add(R.id.parent_frame_layout, new MovieDetailFragment(), MovieDetailFragment.class.getSimpleName());
        } finally {
            transaction.commit();
        }
    }

    @Override
    protected void onStart() {

        Log.d(LOG_TAG, "onStart() -- MovieDetailActivity is about to be become visible.");

        super.onStart();
    }

    @Override
    protected void onResume() {

        Log.i(LOG_TAG, "onResume() -- MovieDetailActivity has become visible and is now resumed.");

        super.onResume();
    }

    @Override
    protected void onPause() {

        Log.i(LOG_TAG, "onPause() -- MovieDetailActivity is about to be paused. Another activity is taking focus.");

        super.onPause();
    }

    @Override
    protected void onStop() {

        Log.i(LOG_TAG, "onStop() -- MovieDetailActivity is stopped and is no longer visible.");

        super.onStop();
    }

    @Override
    protected void onRestart() {

        Log.d(LOG_TAG, "onRestart() -- User came back and MovieDetailActivity is re-displayed to the user.");

        super.onRestart();
    }

    @Override
    protected void onDestroy() {

        Log.i(LOG_TAG, "onDestroy() -- MovieDetailActivity is about to be destroyed.");

        super.onDestroy();
    }
}
