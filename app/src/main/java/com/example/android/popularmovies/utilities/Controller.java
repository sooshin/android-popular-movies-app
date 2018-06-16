package com.example.android.popularmovies.utilities;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller {

    /** The base movie URL from TMDb */
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/";

    public Retrofit getClient() {
        // Use GsonConverterFactory class to generate an implementation of the TheMovieApi interface
        // which uses Gson for its deserialization
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
