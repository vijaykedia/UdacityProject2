package com.kediavijay.popularmovies2.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
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
 * This class will server as adapter class for {@link android.support.v7.widget.RecyclerView} to show movie trailers/videos
 */
public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private static final String LOG_TAG = TrailersAdapter.class.getSimpleName();

    private final ImageLoader imageLoader;
    private final List<String> trailers = new ArrayList<>();

    private int movieId;

    /**
     * Default Constructor
     */
    public TrailersAdapter() {
        imageLoader = PopularMoviesApplication.getInstance().getImageLoader();
    }

    public void setMovieId(final int movieId) {
        this.movieId = movieId;

        final String url = Uri.parse(String.format(Locale.ENGLISH, PopularMoviesConstants.TMDB_MOVIE_TRAILERS_BASE_URL, this.movieId))
                .buildUpon()
                .appendQueryParameter(PopularMoviesConstants.TMDB_DISCOVER_API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build().toString();

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
                        Log.e(LOG_TAG, String.format(Locale.ENGLISH, "Failed to get trailers/videos for movie with id %d", movieId), error);
                    }
                }
        );

        PopularMoviesApplication.getInstance().getRequestQueue().add(request);
    }

    private void updateDataSource(@NonNull final JSONObject response) {

        try {
            final JSONArray result = response.getJSONArray(PopularMoviesConstants.TMDB_API_RESPONSE_RESULTS_KEY);
            for (int i = 0; i < result.length(); i++) {
                final JSONObject trailerInfo = result.getJSONObject(i);
                trailers.add(trailerInfo.getString(PopularMoviesConstants.TRAILER_KEY));
            }
        } catch (final JSONException e) {
            Log.e(LOG_TAG, String.format(Locale.ENGLISH, "Failed to parse tmdb response for fetching trailers for movie with id %d", movieId), e);
        }

    }

    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        Log.i(LOG_TAG, "onCreateViewHolder() -- Creating trailer item view holder.");

        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_trailer_layout, parent, false);
        return new TrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {

        Log.i(LOG_TAG, "onBindViewHolder() -- Updating TrailerViewHolder wih trailers.");

        final String youtubeUrl = String.format(Locale.ENGLISH, "%s%s%s", "http://img.youtube.com/vi/", trailers.get(position), "/hqdefault.jpg");
        imageLoader.get(youtubeUrl, ImageLoader.getImageListener(holder.imageButton, R.drawable.image_load_placeholder, android.R.drawable.ic_dialog_alert));
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        private final ImageButton imageButton;

        public TrailerViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageButton = (ImageButton) itemView.findViewById(R.id.trailer_image_button);
        }
    }
}
