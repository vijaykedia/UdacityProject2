package com.kediavijay.popularmovies2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.adapters.MovieListAdapter;
import com.kediavijay.popularmovies2.contentprovider.MovieInfo;
import com.kediavijay.popularmovies2.fragments.MovieListFragment;
import com.kediavijay.popularmovies2.listener.OnItemClickListener;
import com.kediavijay.popularmovies2.sync.SyncAdapter;
import com.kediavijay.popularmovies2.util.Util;

/**
 * Created by vijaykedia on 10/04/16.
 * This will act as launcher Activity and will the grid of movie posters
 */
public class MovieListActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    private MovieListFragment fragment;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {

        Log.d(LOG_TAG, "OnCreate() -- MovieListActivity is being created.");

        super.onCreate(savedInstanceState);

        // Set Content View
        setContentView(R.layout.general_activity_layout);

        // Initialize preferences with their default values
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);

        // Set Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(4);
            actionBar.setTitle(determineTitle(Util.determineSortOrder(getApplicationContext())));
        }
        
        // Initialize fragment
        fragment = new MovieListFragment();

        // Add fragment
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        try {

            transaction.add(R.id.parent_frame_layout, fragment, MovieListFragment.class.getSimpleName());
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
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        try {
            transaction.detach(fragment);
            transaction.addToBackStack(null);
        } finally {
            transaction.commit();
        }

        Log.i(LOG_TAG, "onPause() -- MovieList fragment has been removed from MovieListActivity.");
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

    @Override
    public boolean onCreateOptionsMenu(@NonNull final Menu menu) {

        Log.d(LOG_TAG, "onCreateOptionsMenu() -- Initialising options menu for MovieListActivity.");

        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {

        Log.d(LOG_TAG, String.format("onOptionsItemSelected() -- User selected an option %s.", item.getTitle()));

        switch (item.getItemId()) {

            case R.id.settings_option:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    private String determineTitle(@NonNull final String sortOrder) {
        switch (sortOrder) {
            case "popularity":
                return "Most Popular";
            case "release_date":
                return "Recently Released";
            case "vote_average":
                return "Highest Rated";
            // Should not reach here
            default:
                return null;
        }
    }

    public static class ClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(@NonNull final MovieInfo movieInfo, MovieListAdapter.MovieImageViewHolder holder) {
            //TODO:
            Log.i(LOG_TAG, "onItemClick() -- Bypassed Detail Activity");
//            Toast.makeText(getApplicationContext(, "Show message", Toast.LENGTH_SHORT).show();
        }
    }
}
