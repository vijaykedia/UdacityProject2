package com.kediavijay.popularmovies2.contentprovider;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kediavijay.popularmovies2.PopularMoviesConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by vijaykedia on 16/04/16.
 * This will contains all summary info for the movie.
 */
@SimpleSQLTable(table = "MovieInfo", provider = "MovieInfoProvider")
public class MovieInfo implements Parcelable {

    private static final String LOG_TAG = MovieInfo.class.getSimpleName();

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);

    @SimpleSQLColumn(value = "movie_id", primary = true)
    public int movieId;

    @SimpleSQLColumn(value = "title")
    public String title;

    @SimpleSQLColumn(value = "overview")
    public String overview;

    @SimpleSQLColumn(value = "popularity")
    public double popularity;

    @SimpleSQLColumn(value = "release_date")
    public long releaseDate;

    @SimpleSQLColumn(value = "poster_path")
    public String posterPath;

    @SimpleSQLColumn(value = "vote_average")
    public double rating;

    @SimpleSQLColumn(value = "favourite")
    public boolean favourite;

    public List<Review> reviews = new ArrayList<>();

    public List<String> trailers = new ArrayList<>();

    /**
     * Default constructor
     */
    public MovieInfo() {
    }

    public MovieInfo(@NonNull final JSONObject response) {
        try {
            movieId = response.getInt(PopularMoviesConstants.MOVIE_ID_KEY);
            title = response.getString(PopularMoviesConstants.MOVIE_TITLE_KEY);
            overview = response.getString(PopularMoviesConstants.MOVIE_OVERVIEW_KEY);
            popularity = response.getDouble(PopularMoviesConstants.MOVIE_POPULARITY_KEY);

            try {
                final Date date = dateFormatter.parse(response.getString(PopularMoviesConstants.MOVIE_RELEASE_DATE_KEY));
                releaseDate = date.getTime();
            } catch (final ParseException ignored) {
            }

            posterPath = response.getString(PopularMoviesConstants.MOVIE_POSTER_PATH_KEY);
            rating = response.getDouble(PopularMoviesConstants.MOVIE_USER_RATING_KEY);
            favourite = false;
        } catch (final JSONException e) {
            Log.e(LOG_TAG, "Failed to parse result and create movieInfo object", e);
        }
    }

    public MovieInfo(@NonNull final Parcel in) {
        movieId = in.readInt();
        title = in.readString();
        overview = in.readString();
        popularity = in.readDouble();
        releaseDate = in.readLong();
        posterPath = in.readString();
        rating = in.readDouble();
        favourite = in.readByte() != 0;

        final Review[] reviewArray = (Review[]) in.readParcelableArray(Review.class.getClassLoader());
        reviews = Arrays.asList(reviewArray);

        final String[] trailerUrls = (String[]) in.readArray(String.class.getClassLoader());
        trailers = Arrays.asList(trailerUrls);
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeInt(movieId);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeDouble(popularity);
        dest.writeLong(releaseDate);
        dest.writeString(posterPath);
        dest.writeDouble(rating);
        dest.writeByte((byte) (favourite ? 1 : 0));

        final Review[] reviewArray = new Review[reviews.size()];
        reviews.toArray(reviewArray);
        dest.writeParcelableArray(reviewArray, flags);

        final String[] trailerUrls = new String[trailers.size()];
        trailers.toArray(trailerUrls);
        dest.writeStringArray(trailerUrls);
    }

    public void addReview(@NonNull final Review review ) {
        reviews.add(review);
    }

    public void addTrailerUrl(@NonNull final String url) {
        trailers.add(url);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "movieId : %s\ntitle : %s\noverView : %s\npopularity : %s\nreleaseDate : %s\nPosterPath : %s\nrating : %s",
                Integer.toString(movieId), title, overview, Double.toString(popularity), Long.toString(releaseDate), posterPath, Double.toString(rating));
    }

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    private static class Review implements Parcelable {

        public String author;

        public String content;

        public Review(@NonNull final String author, @NonNull final String content) {
            this.author = author;
            this.content = content;
        }

        public Review(@NonNull final Parcel in) {
            author = in.readString();
            content = in.readString();
        }

        @Override
        public void writeToParcel(@NonNull final Parcel dest, final int flags) {
            dest.writeString(author);
            dest.writeString(content);
        }

        @Override
        public int describeContents() {
            return hashCode();
        }

        public static final Creator<Review> CREATOR = new Creator<Review>() {
            @Override
            public Review createFromParcel(@NonNull final Parcel in) {
                return new Review(in);
            }

            @Override
            public Review[] newArray(final int size) {
                return new Review[size];
            }
        };
    }
}
