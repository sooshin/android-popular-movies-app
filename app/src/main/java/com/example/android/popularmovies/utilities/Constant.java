/*
 *  Copyright 2018 Soojeong Shin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.BuildConfig;

public final class Constant {

    private Constant() {
        // Restrict instantiation
    }

    /** The base movie URL from TMDb */
    static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/";

    // Constants for MainActivity

    /** Constants that are used to request the network call */
    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String LANGUAGE = "en-US";
    public static final int PAGE = 1;
    public static final String CREDITS = "credits";

    /** API Status code for invalid API key or Authentication failed */
    public static final int RESPONSE_CODE_API_STATUS = 401;

    /** A numeric constant for request code */
    public static final int REQUEST_CODE_DIALOG = 0;

    /** Constant for the span count in the grid layout manager */
    public static final int GRID_SPAN_COUNT = 3;
    /** Constant for the grid spacing (px)*/
    public static final int GRID_SPACING = 8;
    /** True when including edge */
    public static final boolean GRID_INCLUDE_EDGE = true;

    // Constants for DetailActivity

    /** Extra for the movie to be received in the intent */
    public static final String EXTRA_MOVIE = "movie";

    /** The base image URL to build the complete url that is necessary for fetching the image */
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    /** The image file size to build the complete url that is necessary for fetching the image*/
    public static final String BACKDROP_FILE_SIZE ="w500";

    /** The YouTube base URL that is necessary for displaying trailers */
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    /** The YouTube thumbnail base URL that is used to display YouTube video Thumbnails*/
    public static final String YOUTUBE_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    public static final String YOUTUBE_THUMBNAIL_URL_JPG = "/0.jpg";

    /** The base URL used for sharing text*/
    public static final String SHARE_URL = "https://www.themoviedb.org/movie/";

    /** The beginIndex and endIndex to be used for getting a substring of Release Date */
    public static final int RELEASE_YEAR_BEGIN_INDEX = 0;
    public static final int RELEASE_YEAR_END_INDEX = 4;

    /** Type of the share intent data */
    public static final String SHARE_INTENT_TYPE_TEXT = "text/plain";

    // Constant for CastAdapter and MoviePagedListAdapter

    /** The image file size to build the complete url that is necessary for fetching the image */
    public static final String IMAGE_FILE_SIZE = "w185";

    // Constants for DetailPagerAdapter

    /** Constant value for each fragment */
    public static final int INFORMATION = 0;
    public static final int TRAILERS = 1;
    public static final int CAST = 2;
    public static final int REVIEWS = 3;

    /** String array used to display the tap title*/
    public static final String[] TAP_TITLE = new String[] {"Info", "Trailers", "Cast", "Reviews"};

    /** The number of page */
    public static final int PAGE_COUNT = TAP_TITLE.length;

    /** The default value used in the preference summary */
    public static final String DEFAULT_VALUE = "";

    /** Constant for Credits object */
    public static final int BYTE = 0x01;

    /** Pattern used in FormatUtils */
    public static final String PATTERN_FORMAT_NUMBER = "#,###";
    public static final String PATTERN_FORMAT_CURRENCY = "$###,###";
    public static final String PATTERN_FORMAT_DATE_INPUT = "yyyy-MM-dd";
    public static final String PATTERN_FORMAT_DATE_OUTPUT = "MMM dd, yyyy";

    /** Constant used to make ImageView 3:2 aspect ratio or 2:3 aspect ratio */
    public static final int TWO = 2;
    public static final int THREE = 3;

    /** Constant used in GridSpacingItemDecoration */
    public static final int ONE = 1;

    public static final String RESULTS_RUNTIME = "runtime";
    public static final String RESULTS_RELEASE_YEAR = "release_year";
    public static final String RESULTS_GENRE = "genre";

    /** Key for storing the scroll position in MainActivity */
    public static final String LAYOUT_MANAGER_STATE = "layout_manager_state";

    public static final String DATABASE_NAME = "favoritemovies";

    /** The number of threads in the pool used in AppExecutors */
    public static final int NUMBER_OF_THREADS_THREE = 3;

    /** Constant for setCompoundDrawablesWithIntrinsicBounds */
    public static final int DRAWABLES_ZERO = 0;

    /** Constants for pages used in MovieDataSource */
    public static final int PREVIOUS_PAGE_KEY_ONE = 1;
    public static final int NEXT_PAGE_KEY_TWO = 2;
    public static final int PAGE_ONE = 1;

    /** The number of fixed thread pools used in the MainActivityViewModel */
    public static final int NUMBER_OF_FIXED_THREADS_FIVE = 5;

    // Constants used in MainActivityViewModel
    /** Size hint for initial load of PagedList */
    public static final int INITIAL_LOAD_SIZE_HINT = 10;
    /** Size of each page loaded by the PagedList */
    public static final int PAGE_SIZE = 20;
    /** Prefetch distance which defines how far ahead to load */
    public static final int PREFETCH_DISTANCE = 50;

    /** Constants for menu option in FavoriteAdapter */
    public static final String DELETE = "Delete";
    public static final int DELETE_GROUP_ID = 0;
    public static final int DELETE_ORDER = 0;

    /** Constant for formatting runtime */
    public static final int ZERO = 0;
}
