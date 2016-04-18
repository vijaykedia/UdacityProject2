package com.kediavijay.popularmovies2;

/**
 * Created by vijaykedia on 11/04/16.
 * This class will define constants used across application
 */
public class PopularMoviesConstants {

    // Constants related to Tmdb discover movie api request
    public static final String TMDB_DISCOVER_API_BASE_URL = "http://api.themoviedb.org/3/discover/movie";

    public static final String TMDB_DISCOVER_API_KEY = "api_key";

    public static final String TMDB_DISCOVER_API_SORT_ORDER_KEY = "sort_by";

    public static final String TMDB_DISCOVER_API_PAGE_NUMBER_KEY = "page";

    public static final String TMDB_DISCOVER_API_SORT_ORDER_VALUE = "popularity.desc";

    // Base Url for getting movie reviews and movie trailers

    public static final String TMDB_MOVIE_REVIEWS_BASE_URL = "http://api.themoviedb.org/3/movie/%d/reviews";

    public static final String TMDB_MOVIE_TRAILERS_BASE_URL = "http://api.themoviedb.org/3/movie/%d/videos";

    // Constants defining various keys in Tmdb discover movie api response
    public static final String TMDB_API_RESPONSE_RESULTS_KEY = "results";

    public static final String TOTAL_PAGES_KEY = "total_pages";

    public static final String CURRENT_PAGE_KEY = "page";

    public static final String MOVIE_ID_KEY = "id";

    public static final String MOVIE_TITLE_KEY = "title";

    public static final String MOVIE_OVERVIEW_KEY = "overview";

    public static final String MOVIE_POPULARITY_KEY = "popularity";

    public static final String MOVIE_RELEASE_DATE_KEY = "release_date";

    public static final String MOVIE_USER_RATING_KEY = "vote_average";

    public static final String MOVIE_POSTER_PATH_KEY = "poster_path";

    // Constants defining various keys in Tmdb movie review api response
    public static final String REVIEW_AUTHOR_KEY = "author";

    public static final String REVIEW_CONTENT_KEY = "content";

    //
    public static final String TRAILER_KEY = "key";


    // Constant related to Review fragment bundle key
    public static final String MOVIE_ID = "movieId";

    public static final String SELECT_CRITERIA_KEY = "selection_criteria";





}
