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
 * This will contain reviews for movies
 */
@SimpleSQLTable(table = "Review", provider = "MovieInfoProvider")
public class Review {

    @SimpleSQLColumn(value = "_id", primary = true)
    public String _id;

    @SimpleSQLColumn(value = "movie_id")
    public int movieId;

    @SimpleSQLColumn(value = "author")
    public String author;

    @SimpleSQLColumn(value = "content")
    public String content;

    /**
     * Default constructor used by generated ReviewsTable class
     */
    public Review() {
    }

    /**
     * Constructor which will create Reviews object from {@link JSONObject} tmdb movie api response
     *
     * @param movieApiResponse Tmdb movie api response
     */
    public static List<Review> getReviews(@NonNull final JSONObject movieApiResponse) throws JSONException {

        final List<Review> result = new ArrayList<>();

        final JSONObject reviewsJsonObject = movieApiResponse.getJSONObject(PopularMoviesConstants.REVIEWS_RESPENSE_SET_KEY);
        final JSONArray resultsJsonArray = reviewsJsonObject.getJSONArray(PopularMoviesConstants.TMDB_API_RESPONSE_RESULTS_KEY);
        for (int i = 0; i < resultsJsonArray.length(); i++) {
            final JSONObject reviewJsonObject = resultsJsonArray.getJSONObject(i);
            final Review review = new Review();

            review.movieId = movieApiResponse.getInt(PopularMoviesConstants.TMDB_API_RESPONSE_ID_KEY);

            review._id = reviewJsonObject.getString(PopularMoviesConstants.TMDB_API_RESPONSE_ID_KEY);
            review.author = reviewJsonObject.getString(PopularMoviesConstants.REVIEW_AUTHOR_KEY);
            review.content = reviewJsonObject.getString(PopularMoviesConstants.REVIEW_CONTENT_KEY);

            result.add(review);
        }

        return result;
    }
}
