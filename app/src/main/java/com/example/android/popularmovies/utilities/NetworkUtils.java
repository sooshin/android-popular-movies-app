package com.example.android.popularmovies.utilities;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * NetworkUtils is used to communicate with the movie database servers.
 */
public final class NetworkUtils {

    /** Tag for the log message */
    private static final String TAG = NetworkUtils.class.getSimpleName();

    /** The base movie URL from TMDb */
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/";

    /** Constant value for the movie */
    private static final String MOVIE_PATH = "movie";
    /** Constant value for api key parameter */
    private static final String API_KEY_PARAM = "api_key";


    /** Use your API Key */
    private static final String API_KEY = "YOUR_API_KEY";

    /**
     * Builds the URL used to talk to the movie server using a sort criteria.
     * @param sortCriteria is a query for sorting movies by popularity or top rating.
     * @return The URL to use to query the movie server.
     */
    public static URL buildMovieUrl(String sortCriteria) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(sortCriteria)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem with building URL");
        }

        Log.d(TAG, "Built Movie URL " + url);
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
