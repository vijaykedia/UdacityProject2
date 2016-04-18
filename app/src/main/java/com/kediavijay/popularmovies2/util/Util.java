package com.kediavijay.popularmovies2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.kediavijay.popularmovies2.R;

/**
 * Created by vijaykedia on 16/04/16.
 * This will have various utility functions used across app in various modules
 */
public class Util {

    public static String determineSortOrder(@NonNull final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.movie_sort_order_key), null);
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

    public static String getTMDBImageUrl(@NonNull final String posterPath) {
        return String.format("%s%s%s", "http://image.tmdb.org/t/p/", "w500", posterPath);
    }

    public static boolean isTablet(@NonNull final Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
