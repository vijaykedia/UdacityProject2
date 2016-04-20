package com.kediavijay.popularmovies2.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.fragments.MovieListFragment;
import com.kediavijay.popularmovies2.sync.SyncAdapter;

/**
 * Created by vijaykedia on 10/04/16.
 * This will act as launcher Activity and will the grid of movie posters
 */
public class MovieListActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {

        Log.d(LOG_TAG, "OnCreate() -- MovieListActivity is being created.");

        super.onCreate(savedInstanceState);

        // Set Content View
        setContentView(R.layout.general_parent_container_layout);

        // Add fragment
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        try {
            transaction.add(R.id.parent_container_content, new MovieListFragment(), MovieListFragment.class.getSimpleName());
        } finally {
            transaction.commit();
        }

        // Get Dummy account and run sync for that
        SyncAdapter.initializeSyncAdapter(getApplicationContext());
    }

    @Override
    protected void onStart() {

        Log.d(LOG_TAG, "OnStart() -- MovieListActivity is about to be become visible.");

        super.onStart();
    }

    @Override
    protected void onResume() {

        Log.d(LOG_TAG, "onResume() -- MovieListActivity has become visible and is now resumed.");

        super.onResume();
    }

    @Override
    protected void onPause() {

        Log.d(LOG_TAG, "onPause() -- MovieListActivity is about to be paused. Another activity is taking focus.");

        super.onPause();
    }

    @Override
    protected void onStop() {

        Log.d(LOG_TAG, "onStop() -- MovieListActivity is stopped and is no longer visible.");

        super.onStop();
    }

    @Override
    protected void onRestart() {

        Log.d(LOG_TAG, "onRestart() -- User came back and MovieListActivity is re-displayed to the user.");

        super.onRestart();
    }

    @Override
    protected void onDestroy() {

        Log.d(LOG_TAG, "onDestroy() -- MovieListActivity is about to be destroyed.");

        super.onDestroy();
    }
}
