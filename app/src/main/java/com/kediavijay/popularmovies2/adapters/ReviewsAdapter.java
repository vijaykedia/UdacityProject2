package com.kediavijay.popularmovies2.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kediavijay.popularmovies2.BuildConfig;
import com.kediavijay.popularmovies2.PopularMoviesApplication;
import com.kediavijay.popularmovies2.PopularMoviesConstants;
import com.kediavijay.popularmovies2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vijaykedia on 10/04/16.
 * This class will server as adapter class for {@link android.support.v7.widget.RecyclerView} to show movie reviews
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private static final String LOG_TAG = ReviewsAdapter.class.getSimpleName();

    private int movieId;
    private final List<Pair<String, String>> reviews = new ArrayList<>();

    /**
     * Default Constructor
     */
    public ReviewsAdapter(){
    }

    public void setMovieId(final int movieId) {
        this.movieId = movieId;

        final String url = Uri.parse(String.format(Locale.ENGLISH, PopularMoviesConstants.TMDB_MOVIE_REVIEWS_BASE_URL, this.movieId))
                .buildUpon()
                .appendQueryParameter(PopularMoviesConstants.TMDB_DISCOVER_API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build().toString();

        Log.i(LOG_TAG, String.format(Locale.ENGLISH, "url called : %s", url));

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull final JSONObject response) {
                        updateDataSource(response);
                        notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull final VolleyError error) {
                        Log.e(LOG_TAG, String.format(Locale.ENGLISH, "Failed to get review for movie with id %d", movieId), error);
                    }
                }
        );

        PopularMoviesApplication.getInstance().getRequestQueue().add(request);
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        Log.i(LOG_TAG, "onCreateViewHolder() -- Creating review item view holder.");

        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_review_layout, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewViewHolder holder, final int position) {

        Log.i(LOG_TAG, "onBindViewHolder() -- Updating ReviewViewHolder wih review content.");

        final Pair<String, String> review = reviews.get(position);
        holder.author.setText(review.first);
        holder.content.setText(review.second);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    private void updateDataSource(@NonNull final JSONObject response) {
        try {
            final JSONArray result = response.getJSONArray(PopularMoviesConstants.TMDB_API_RESPONSE_RESULTS_KEY);
            for (int i = 0; i < result.length(); i++) {
                final JSONObject reviewItem = result.getJSONObject(i);
                reviews.add(new Pair<>(reviewItem.getString(PopularMoviesConstants.REVIEW_AUTHOR_KEY), reviewItem.getString(PopularMoviesConstants.REVIEW_CONTENT_KEY)));
            }
        } catch (final JSONException e) {
            Log.e(LOG_TAG, String.format(Locale.ENGLISH, "Failed to parse tmdb response for fetching reviews for movie with id %d", movieId), e);
        }
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        private final TextView author;
        private final TextView content;

        public ReviewViewHolder(@NonNull final View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.review_author);
            content = (TextView) itemView.findViewById(R.id.review_content);
        }
    }
}
