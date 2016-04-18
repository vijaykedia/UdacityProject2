package com.kediavijay.popularmovies2.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kediavijay.popularmovies2.BuildConfig;
import com.kediavijay.popularmovies2.PopularMoviesApplication;
import com.kediavijay.popularmovies2.PopularMoviesConstants;
import com.kediavijay.popularmovies2.R;
import com.kediavijay.popularmovies2.contentprovider.MovieInfo;
import com.kediavijay.popularmovies2.contentprovider.MovieInfoTable;
import com.kediavijay.popularmovies2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by vijaykedia on 11/04/16.
 * <p/>
 * Handle the transfer of data from TheMovieDB server and an
 * app, using the Android sync adapter framework.
 * <p/>
 * This class is instantiated in {@link SyncService} which also binds {@link SyncAdapter} to the system
 * {@link SyncAdapter} should only be initialized in {@link SyncService}.
 * <p/>
 * The system calls {@link SyncAdapter#onPerformSync(Account, Bundle, String, ContentProviderClient, SyncResult)}
 * via an RPC call through {@link android.os.IBinder} object supplied by {@link SyncService}
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = SyncAdapter.class.getSimpleName();

    private static final Uri AUTHENTICATED_DISCOVER_MOVIE_URL = Uri.parse(PopularMoviesConstants.TMDB_DISCOVER_API_BASE_URL)
            .buildUpon()
            .appendQueryParameter(PopularMoviesConstants.TMDB_DISCOVER_API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY).build();

    private final ContentResolver mContentResolver;

    // -------------- Constructors -------------------

    public SyncAdapter(@NonNull final Context context, final boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(@NonNull final Context context, final boolean autoInitialize, final boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Perform a sync for this account.
     *
     * @param account    the account that should be synced
     * @param extras     SyncAdapter-specific parameters
     * @param authority  the authority of this sync request
     * @param provider   a ContentProviderClient that points to the ContentProvider for this
     *                   authority
     * @param syncResult SyncAdapter-specific parameters
     */
    @Override
    public void onPerformSync(@NonNull final Account account, @NonNull final Bundle extras, @NonNull final String authority, @NonNull final ContentProviderClient provider, @NonNull final SyncResult syncResult) {

        Log.i(LOG_TAG, "onPerformSync() -- Starting sync");

        fetchMoviesData();

        Log.i(LOG_TAG, "onPerformSync() -- Synchronization complete");

    }

    /**
     * Helper method which will load data in the background
     */
    private void fetchMoviesData() {

        final String sortOrder = Util.determineSortOrder(getContext());
        final int currentPage = Util.determineCurrentPage(getContext(), sortOrder);

        final String url = AUTHENTICATED_DISCOVER_MOVIE_URL.buildUpon()
                .appendQueryParameter(PopularMoviesConstants.TMDB_DISCOVER_API_SORT_ORDER_KEY, sortOrder + ".desc")
                .appendQueryParameter(PopularMoviesConstants.TMDB_DISCOVER_API_PAGE_NUMBER_KEY, Integer.toString(currentPage + 1))
                .build()
                .toString();

        Log.d(LOG_TAG, String.format(Locale.ENGLISH, "fetchMoviesData() -- Url called : %s", url));

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull final JSONObject response) {
                        try {
                            updateLocalDatabase(response);
                            Util.updateCurrentAndTotalPage(getContext(), sortOrder, response.getInt(PopularMoviesConstants.CURRENT_PAGE_KEY), response.getInt(PopularMoviesConstants.TOTAL_PAGES_KEY));
                        } catch (final JSONException e) {
                            Log.e(LOG_TAG, "Failed to parse tmdb discover api json response", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull final VolleyError error) {
                        Log.e(LOG_TAG, "Failed to get movie data from tmdb server", error);
                    }
                });

        PopularMoviesApplication.getInstance().getRequestQueue().add(jsonRequest);
    }

    /**
     * This will update the local sqlite database.
     * @param response tmdb discover api response
     * @throws JSONException
     */
    public void updateLocalDatabase(@NonNull final JSONObject response) throws JSONException {

        final JSONArray movieResult = response.getJSONArray(PopularMoviesConstants.TMDB_API_RESPONSE_RESULTS_KEY);

        for (int i = 0; i < movieResult.length(); i++) {

            final JSONObject movieData = movieResult.getJSONObject(i);
            final MovieInfo movieInfo = new MovieInfo(movieData);

            final ContentValues value = MovieInfoTable.getContentValues(movieInfo, true);

            final Uri checkUri = ContentUris.withAppendedId(MovieInfoTable.CONTENT_URI, movieInfo.movieId);
            final Cursor cursor = mContentResolver.query(checkUri, null, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {

                Log.d(LOG_TAG, "updateLocalDatabase() -- Returned cursor is null. Inserting row.");

                final Uri returnedUri = mContentResolver.insert(MovieInfoTable.CONTENT_URI, value);

                if (returnedUri == null) {
                    Log.e(LOG_TAG, "updateLocalDatabase() -- Failed to insert record in database.");
                }
            } else {

                Log.d(LOG_TAG, "updateLocalDatabase() -- Cursor is not null, it means there already exist an entry");

                final int rowsUpdated = mContentResolver.update(checkUri, value, null, null);
                if (rowsUpdated < 1) {
                    Log.e(LOG_TAG, String.format(Locale.ENGLISH, "updateLocalDatabase() -- Failed to update record in database for Uri : %s", checkUri.toString()));
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * This will initialize syncAdapter and run sync process
     */
    public static void initializeSyncAdapter(@NonNull final Context context) {
        getSyncAccount(context);
    }

    /**
     * This method is responsible for creating a dummy account which can be used by Sync framework
     * Reference : http://developer.android.com/training/sync-adapters/creating-sync-adapter.html#CreateSyncAdapterMetadata
     *
     * @return instance of {@link Account}
     */
    public static Account getSyncAccount(@NonNull final Context context) {

        // Create the account type and default account
        final Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // Get an instance of the Android account manager
        final AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

            // Add the account and account type, no password or user data. If successful, return the Account object, otherwise report an error.
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(@NonNull final Account account, @NonNull final Context context) {

        ContentResolver.setSyncAutomatically(account, context.getString(R.string.content_authority), true);

        syncNow(context);
    }

    public static void syncNow(@NonNull final Context context) {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }
}
