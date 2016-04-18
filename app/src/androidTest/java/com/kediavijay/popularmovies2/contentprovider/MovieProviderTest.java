package com.kediavijay.popularmovies2.contentprovider;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;
import android.text.TextUtils;
import android.util.Log;

import com.kediavijay.popularmovies2.PopularMoviesConstants;
import com.kediavijay.popularmovies2.R;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vijaykedia on 12/04/16.
 * This class will test basic functionality of content provider
 */

@RunWith(AndroidJUnit4.class)
public class MovieProviderTest extends ProviderTestCase2<MovieInfoProvider> {

    private static final String LOG_TAG = MovieProviderTest.class.getSimpleName();

    /**
     * Constructor.
     */
    public MovieProviderTest() {
        super(MovieInfoProvider.class, MovieInfoProvider.AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
        getContext().deleteDatabase("movie_info.db");
    }

    @Test
    public void testDataInsert() throws JSONException, RemoteException {

        final ContentProviderClient client = getContentProviderClient();

        final JSONArray movieResult = getValues();
        Assert.assertTrue(movieResult.length() > 0);

        if (client != null) {
            for (int i = 0; i < movieResult.length(); i++) {

                final JSONObject movieData = movieResult.getJSONObject(i);
                final MovieInfo movieInfo = new MovieInfo(movieData);

                Log.d(LOG_TAG, String.format("Movie Info : %s\n\n\n\n\n", movieInfo.toString()));

                final ContentValues value = MovieInfoTable.getContentValues(movieInfo, true);

                final Cursor cursor = client.query(ContentUris.withAppendedId(MovieInfoTable.CONTENT_URI, movieInfo.movieId), null, null, null, null);
                if (cursor == null) {

                    Log.d(LOG_TAG, "Returned cursor is null. Inserting row.");

                    final Uri returnedUri = client.insert(MovieInfoTable.CONTENT_URI, value);

                    if (returnedUri != null) {
                        Log.d(LOG_TAG, String.format(Locale.ENGLISH, "Returned Uri : %s", returnedUri.toString()));
                    } else {
                        Log.d(LOG_TAG, "returned Uri is null");
                    }
                } else {

                    if (!cursor.moveToFirst()){

                        Log.d(LOG_TAG, "Returned cursor is null. Inserting row.");

                        final Uri returnedUri = client.insert(MovieInfoTable.CONTENT_URI, value);

                        if (returnedUri != null) {
                            Log.d(LOG_TAG, String.format(Locale.ENGLISH, "Returned Uri : %s", returnedUri.toString()));
                        } else {
                            Log.d(LOG_TAG, "returned Uri is null");
                        }
                    }

                    Log.d(LOG_TAG, "cursor is not null, it means there already exist an entry");
                    Log.d(LOG_TAG, cursor.toString());

                    final MovieInfo returnedMovieInfo = MovieInfoTable.getRow(cursor, false);
                    Log.d(LOG_TAG, String.format(Locale.ENGLISH, "Returned Info : %s", returnedMovieInfo.toString()));

                    client.update(ContentUris.withAppendedId(MovieInfoTable.CONTENT_URI, movieInfo.movieId), value, null, null);
                    cursor.close();
                }

            }
        }
    }

    private JSONArray getValues() {

        final InputStream inputStream = getContext().getResources().openRawResource(R.raw.discover_movie_response);
        final String movieDataString = convertStreamToString(inputStream);

        Log.i(LOG_TAG, movieDataString);

        Assert.assertFalse(TextUtils.isEmpty(movieDataString));

        final List<ContentValues> response = new ArrayList<>();

        try {
            final JSONObject movieDataJson = new JSONObject(movieDataString);

            return movieDataJson.getJSONArray(PopularMoviesConstants.TMDB_API_RESPONSE_RESULTS_KEY);
        } catch (final JSONException e) {
            Log.e(LOG_TAG, "Failed to parse json movie data", e);
            return new JSONArray();
        }
    }

    private String convertStreamToString(@NonNull final InputStream is) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        final StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (final IOException e) {
            Log.e(LOG_TAG, "Failed to read data from resource file.", e);
        } finally {
            try {
                reader.close();
            } catch (final IOException e) {
                Log.e(LOG_TAG, "Failed to close reader", e);
            }
        }
        return sb.toString();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        getContext().deleteDatabase("movie_info.db");
    }

    @Nullable
    private ContentProviderClient getContentProviderClient() {
        final ContentResolver resolver = getMockContentResolver();
        return resolver.acquireContentProviderClient(MovieInfoTable.CONTENT_URI);
    }
}
