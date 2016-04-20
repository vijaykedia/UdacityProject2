package com.kediavijay.popularmovies2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.kediavijay.popularmovies2.R;

import java.util.Locale;

/**
 * Created by vijaykedia on 16/04/16.
 * This will have various utility functions used across app in various modules
 */
public class Util {

    @NonNull
    public static String determineSortOrder(@NonNull final String selection) {
        switch (selection) {
            case "Most Popular":
                return "popularity";
            case "Recently Released":
                return "release_date";
            case "Highest Rated":
                return "vote_average";
            // Should not reach here
            default:
                throw new RuntimeException(String.format(Locale.ENGLISH, "determineSortOrder() -- App shouldn't reach this stage. selection passed : %s ", selection));
        }
    }

    @NonNull
    public static String getSortOrder(@NonNull final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.movie_sort_order_key), context.getString(R.string.default_movie_sort_order));
    }

    public static void setSortOrder(@NonNull final Context context, @NonNull final String sortOrder) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(context.getString(R.string.movie_sort_order_key), sortOrder);
        editor.apply();
    }

    public static int determineCurrentPage(@NonNull final Context context, @NonNull final String sortOrder) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String currentPageKey;

        switch (sortOrder) {
            case "popularity":
                currentPageKey = context.getString(R.string.current_synced_page_popularity_sort_order);
                break;
            case "release_date":
                currentPageKey = context.getString(R.string.current_synced_page_released_date_sort_order);
                break;
            case "vote_average":
                currentPageKey = context.getString(R.string.current_synced_page_rating_sort_order);
                break;
            default:
                return 0;
        }


        return sharedPreferences.getInt(currentPageKey, 0);
    }

    public static int determineTotalPage(@NonNull final Context context, @NonNull final String sortOrder) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String totalPagesKey;

        switch (sortOrder) {
            case "popularity":
                totalPagesKey = context.getString(R.string.total_pages_popularity_sort_order);
                break;
            case "release_date":
                totalPagesKey = context.getString(R.string.total_pages_released_date_sort_order);
                break;
            case "vote_average":
                totalPagesKey = context.getString(R.string.total_pages_rating_sort_order);
                break;
            default:
                return 1;
        }


        return sharedPreferences.getInt(totalPagesKey, 1);
    }

    public static void updateCurrentAndTotalPage(@NonNull final Context context, @NonNull final String sortOrder, final int currentPage, final int totalPage) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final String currentPageKey;
        final String totalPagesKey;

        switch (sortOrder) {
            case "popularity":
                currentPageKey = context.getString(R.string.current_synced_page_popularity_sort_order);
                totalPagesKey = context.getString(R.string.total_pages_popularity_sort_order);
                break;
            case "release_date":
                currentPageKey = context.getString(R.string.current_synced_page_released_date_sort_order);
                totalPagesKey = context.getString(R.string.total_pages_released_date_sort_order);
                break;
            case "vote_average":
                currentPageKey = context.getString(R.string.current_synced_page_rating_sort_order);
                totalPagesKey = context.getString(R.string.total_pages_rating_sort_order);
                break;
            default:
                return;
        }

        editor.putInt(currentPageKey, currentPage);
        editor.putInt(totalPagesKey, totalPage);
        editor.apply();
    }

    @NonNull
    public static String getTMDBPosterImageUrl(@NonNull final String posterPath) {
        return String.format("%s%s%s", "http://image.tmdb.org/t/p/", "w500", posterPath);
    }

    @NonNull
    public static String getTMDBBackdropImageUrl(@NonNull final String posterPath) {
        return String.format("%s%s%s", "http://image.tmdb.org/t/p/", "original", posterPath);
    }

    public static boolean isTablet(@NonNull final Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isSyncRunning(@NonNull final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.sync_service_running), false);
    }

    public static void setSyncCompleted(@NonNull final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(context.getString(R.string.sync_service_running), false);
        editor.apply();
    }

    public static void setSyncRunning(@NonNull final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(context.getString(R.string.sync_service_running), true);
        editor.apply();
    }
}
