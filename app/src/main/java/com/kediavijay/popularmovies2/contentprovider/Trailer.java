package com.kediavijay.popularmovies2.contentprovider;

import android.support.annotation.NonNull;

import com.kediavijay.popularmovies2.PopularMoviesConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by vijaykedia on 19/04/16.
 * This will contain trailers for movies
 */
@SimpleSQLTable(table = "Trailer", provider = "MovieInfoProvider")
public class Trailer {

    @SimpleSQLColumn(value = "_id", primary = true)
    public String _id;

    @SimpleSQLColumn(value = "movie_id")
    public int movieId;

    @SimpleSQLColumn(value = "name")
    public String name;

    @SimpleSQLColumn(value = "key")
    public String youtubeUrlKey;


    /**
     * Default constructor used by generated ReviewsTable class
     */
    public Trailer() {
    }

    /**
     * Constructor which will create Reviews object from {@link JSONObject} tmdb movie api response
     *
     * @param movieApiResponse Tmdb movie api response
     */
    public static List<Trailer> getTrailers(@NonNull final JSONObject movieApiResponse) throws JSONException {

        final List<Trailer> result = new ArrayList<>();

        final JSONObject trailersJsonObject = movieApiResponse.getJSONObject(PopularMoviesConstants.VIDEOS_RESPONSE_SET_KEY);

        final JSONArray resultsJsonArray = trailersJsonObject.getJSONArray(PopularMoviesConstants.TMDB_API_RESPONSE_RESULTS_KEY);
        for (int i = 0; i < resultsJsonArray.length(); i++) {
            final JSONObject trailerJsonObject = resultsJsonArray.getJSONObject(i);
            final Trailer trailer = new Trailer();
            trailer.movieId = movieApiResponse.getInt(PopularMoviesConstants.TMDB_API_RESPONSE_ID_KEY);

            trailer._id = trailerJsonObject.getString(PopularMoviesConstants.TMDB_API_RESPONSE_ID_KEY);
            trailer.name = trailerJsonObject.getString(PopularMoviesConstants.TRAILER_NAME_KEY);
            trailer.youtubeUrlKey = trailerJsonObject.getString(PopularMoviesConstants.TRAILER_YOUTUBE_KEY);

            result.add(trailer);
        }

        return result;
    }
}
