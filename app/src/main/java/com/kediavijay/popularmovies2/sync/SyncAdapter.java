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
import com.kediavijay.popularmovies2.contentprovider.Review;
import com.kediavijay.popularmovies2.contentprovider.ReviewTable;
import com.kediavijay.popularmovies2.contentprovider.Trailer;
import com.kediavijay.popularmovies2.contentprovider.TrailerTable;
import com.kediavijay.popularmovies2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
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

        // Check if there any already a sync running on
        if (!Util.isSyncRunning(getContext())) {

            Log.i(LOG_TAG, "onPerformSync() -- Starting sync");

            // First set the status of sync to running, so that next instance of sync will skip this process
            Util.setSyncRunning(getContext());

            // Update movie Data
            fetchMoviesDataBasedOnSortOrder();

            Log.i(LOG_TAG, "onPerformSync() -- Synchronization complete");

        } else {
            Log.i(LOG_TAG, "onPerformSync() -- Sync is already running. Skipping this run.");
        }
    }

    /**
     * Helper method which will call tmdb discover api for given sort order (stored in shared preferences) and load data in the background
     */
    private void fetchMoviesDataBasedOnSortOrder() {

        final String sortOrder = Util.getSortOrder(getContext());
        final int currentPage = Util.determineCurrentPage(getContext(), sortOrder);

        final String discover_movie_url = addAuthenticationAndReturnUri(PopularMoviesConstants.TMDB_DISCOVER_API_BASE_URL)
                .buildUpon()
                .appendQueryParameter(PopularMoviesConstants.TMDB_DISCOVER_API_SORT_ORDER_KEY, sortOrder + ".desc")
                .appendQueryParameter(PopularMoviesConstants.TMDB_DISCOVER_API_PAGE_NUMBER_KEY, Integer.toString(currentPage + 1))
                .build()
                .toString();

        Log.v(LOG_TAG, String.format(Locale.ENGLISH, "fetchMoviesDataBasedOnSortOrder() -- Url called : %s", discover_movie_url));

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, discover_movie_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull final JSONObject response) {
                        try {
                            extractMovieIdAndFetchData(response);

                            Util.updateCurrentAndTotalPage(getContext(), sortOrder, response.getInt(PopularMoviesConstants.CURRENT_PAGE_KEY), response.getInt(PopularMoviesConstants.TOTAL_PAGES_KEY));

                            // Set sync as completed, so that next time sync can be run.
                            Util.setSyncCompleted(getContext());

                        } catch (final JSONException e) {
                            Log.e(LOG_TAG, "fetchMoviesDataBasedOnSortOrder() -- Failed to parse tmdb discover api json response", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull final VolleyError error) {
                        Log.e(LOG_TAG, "fetchMoviesDataBasedOnSortOrder() -- Failed to get movie data from tmdb server", error);
                    }
                });

        PopularMoviesApplication.getInstance().getRequestQueue().add(jsonRequest);
    }

    /**
     * Helper function to iterate over discover movie api response and then call movie api response,
     * so that all data can be saved in database.
     *
     * @param response discover movie api response
     * @throws JSONException
     */
    private void extractMovieIdAndFetchData(@NonNull final JSONObject response) throws JSONException {

        final JSONArray movieResult = response.getJSONArray(PopularMoviesConstants.TMDB_API_RESPONSE_RESULTS_KEY);

        for (int i = 0; i < movieResult.length(); i++) {
            final JSONObject movieInfo = movieResult.getJSONObject(i);
            addDelay();
            fetchMovieDataById(movieInfo.getInt(PopularMoviesConstants.TMDB_API_RESPONSE_ID_KEY));
        }
    }

    /**
     * Helper function which will call tmdb movie api for given movie id and load data in background
     *
     * @param movieId movieId
     */
    private void fetchMovieDataById(final int movieId) {

        final String movieIdUrl = addAuthenticationAndReturnUri(String.format(Locale.ENGLISH, PopularMoviesConstants.TMDB_MOVIE_API_BASE_URL, movieId))
                .buildUpon()
                .appendQueryParameter(PopularMoviesConstants.TMDB_MOVIE_API_APPENDER_KEY, "reviews,videos")
                .build()
                .toString();

        Log.v(LOG_TAG, String.format(Locale.ENGLISH, "fetchMovieDataById() -- Url called : %s", movieIdUrl));

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, movieIdUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull final JSONObject response) {
                        try {
                            updateLocalDatabase(response);
                        } catch (final JSONException e) {
                            Log.e(LOG_TAG, "fetchMovieDataById() -- Failed to parse tmdb movie api response.", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull final VolleyError error) {
                        Log.e(LOG_TAG, "fetchMovieDataById() -- Failed to fetch movie data", error);
                    }
                }
        );
        PopularMoviesApplication.getInstance().getRequestQueue().add(request);
    }

    /**
     * This will update the local sqlite database.
     *
     * @param response tmdb discover api response
     * @throws JSONException
     */
    public void updateLocalDatabase(@NonNull final JSONObject response) throws JSONException {

        final MovieInfo movieInfo = new MovieInfo(response);
        final List<Review> reviews = Review.getReviews(response);
        final List<Trailer> trailers = Trailer.getTrailers(response);

        final ContentValues movieInfoContentValue = MovieInfoTable.getContentValues(movieInfo, true);

        final ContentValues[] reviewContentValues = new ContentValues[reviews.size()];
        int i = 0;
        for (final Review review : reviews) {
            reviewContentValues[i++] = ReviewTable.getContentValues(review, true);
        }

        final ContentValues[] trailerContentValues = new ContentValues[trailers.size()];
        i = 0;
        for (final Trailer trailer : trailers) {
            trailerContentValues[i++] = TrailerTable.getContentValues(trailer, true);
        }

        updateMovieInfoInDatabase(movieInfoContentValue, movieInfo.movieId);
        updateReviewsInDatabase(reviewContentValues);
        updateTrailersInDatabase(trailerContentValues);
    }

    /**
     * Helper function to update database with movieInfo
     */
    public void updateMovieInfoInDatabase(@NonNull final ContentValues contentValues, final int movieId) {

        final Uri checkUri = ContentUris.withAppendedId(MovieInfoTable.CONTENT_URI, movieId);
        final Cursor cursor = mContentResolver.query(checkUri, new String[]{MovieInfoTable.FIELD_MOVIE_ID}, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) {

            Log.d(LOG_TAG, "updateMovieInfoInDatabase() -- Returned cursor is null. Inserting movieInfo in database.");

            final Uri returnedUri = mContentResolver.insert(MovieInfoTable.CONTENT_URI, contentValues);

            if (returnedUri == null) {
                Log.e(LOG_TAG, "updateMovieInfoInDatabase() -- Failed to insert record in database.");
            }
        } else {

            Log.d(LOG_TAG, "updateMovieInfoInDatabase() -- Cursor is not null, it means there already exist an entry");

            final int rowsUpdated = mContentResolver.update(checkUri, contentValues, null, null);
            if (rowsUpdated < 1) {
                Log.e(LOG_TAG, String.format(Locale.ENGLISH, "updateMovieInfoInDatabase() -- Failed to update record in database for Uri : %s", checkUri.toString()));
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * Helper function to update database with review
     */
    private void updateReviewsInDatabase(@NonNull final ContentValues[] reviewContentValues) {

        for (final ContentValues contentValues : reviewContentValues) {

            final String reviewId = contentValues.getAsString(TrailerTable.FIELD__ID);

            final Cursor cursor = mContentResolver.query(ReviewTable.CONTENT_URI, new String[]{ReviewTable.FIELD__ID}, ReviewTable.FIELD__ID + " = ?", new String[]{reviewId}, null);

            if (cursor == null || !cursor.moveToFirst()) {

                Log.d(LOG_TAG, "updateReviewsInDatabase() -- Returned cursor is null. Inserting review info in database.");

                final Uri returnedUri = mContentResolver.insert(ReviewTable.CONTENT_URI, contentValues);

                if (returnedUri == null) {
                    Log.e(LOG_TAG, "updateReviewsInDatabase() -- Failed to insert record in database.");
                }
            } else {

                Log.d(LOG_TAG, "updateReviewsInDatabase() -- Cursor is not null, it means there already exist an entry");

                final int rowsUpdated = mContentResolver.update(ReviewTable.CONTENT_URI, contentValues, ReviewTable.FIELD__ID + " = ?", new String[]{reviewId});
                if (rowsUpdated < 1) {
                    Log.e(LOG_TAG, String.format(Locale.ENGLISH, "updateReviewsInDatabase() -- Failed to update record in database for review with id : %s", reviewId));
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Helper function to update database with trailers
     */
    private void updateTrailersInDatabase(@NonNull final ContentValues[] trailerContentValues) {

        for (final ContentValues contentValues : trailerContentValues) {

            final String trailerId = contentValues.getAsString(TrailerTable.FIELD__ID);

            final Cursor cursor = mContentResolver.query(TrailerTable.CONTENT_URI, new String[]{TrailerTable.FIELD__ID}, TrailerTable.FIELD__ID + " = ?", new String[] {trailerId}, null);

            if (cursor == null || !cursor.moveToFirst()) {

                Log.d(LOG_TAG, "updateTrailersInDatabase() -- Returned cursor is null. Inserting trailer info in database.");

                final Uri returnedUri = mContentResolver.insert(TrailerTable.CONTENT_URI, contentValues);

                if (returnedUri == null) {
                    Log.e(LOG_TAG, "updateTrailersInDatabase() -- Failed to insert record in database.");
                }
            } else {

                Log.d(LOG_TAG, "updateTrailersInDatabase() -- Cursor is not null, it means there already exist an entry");

                final int rowsUpdated = mContentResolver.update(TrailerTable.CONTENT_URI, contentValues, TrailerTable.FIELD__ID + " = ?", new String[] {trailerId});
                if (rowsUpdated < 1) {
                    Log.e(LOG_TAG, String.format(Locale.ENGLISH, "updateTrailersInDatabase() -- Failed to update record in database for trailer with id : %s", trailerId));
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
            if (!accountManager.addAccountExplicitly(newAccount, "dummy", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        }
        return newAccount;
    }

    /**
     * Helper method to add authentication (api_key) to tmdb requests
     */
    private Uri addAuthenticationAndReturnUri(@NonNull final String baseUrl) {
        return Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter(PopularMoviesConstants.TMDB_API_AUTH_KEY, BuildConfig.THE_MOVIE_DB_API_KEY).build();
    }

    /**
     * This is used to add delay between two requests
     */
    private void addDelay() {
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
