package com.kediavijay.popularmovies2.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.fragments.SettingsFragment;

/**
 * Created by vijaykedia on 10/04/16.
 * This activity will allow user to change settings.
 *
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreate() -- SettingsActivity is being created.");

        super.onCreate(savedInstanceState);

        // Set ContentView
        setContentView(R.layout.general_activity_layout);

        // Set Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(4);
            actionBar.setTitle("Settings");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Since Setting is not a view but preference screen, instead of adding content view in fragment,
        // replace parent layout (FrameLayout) in this case.
        final FragmentManager manager = getFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        try {
            transaction.replace(R.id.parent_frame_layout, new SettingsFragment());
        } finally {
            transaction.commit();
        }
    }

    @Override
    protected void onStart() {

        Log.d(LOG_TAG, "onStart() -- SettingsActivity is about to be become visible.");

        super.onStart();
    }

    @Override
    protected void onResume() {

        Log.d(LOG_TAG, "onResume() -- SettingsActivity has become visible and is now resumed.");

        super.onResume();
    }

    @Override
    protected void onPause() {

        Log.d(LOG_TAG, "onPause() -- SettingsActivity is about to be paused. Another activity is taking focus.");

        super.onPause();
    }

    @Override
    protected void onStop() {

        Log.d(LOG_TAG, "onStop() -- SettingsActivity is stopped and is no longer visible.");

        super.onStop();
    }

    @Override
    protected void onRestart() {

        Log.d(LOG_TAG, "onRestart() -- User came back and SettingsActivity is re-displayed to the user.");

        super.onRestart();
    }

    @Override
    protected void onDestroy() {

        Log.d(LOG_TAG, "onDestroy() -- SettingsActivity is about to be destroyed.");

        super.onDestroy();
    }
}
