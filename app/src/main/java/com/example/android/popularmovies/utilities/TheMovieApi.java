package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.model.MovieDetails;
import com.example.android.popularmovies.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * The movie database api interface. Retrofit turns HTTP API into a Java interface.
 */
public interface TheMovieApi {

    @GET("movie/{sort_criteria}")
    Call<MovieResponse> getMovies(
            @Path("sort_criteria") String sortCriteria,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("movie/{id}")
    Call<MovieDetails> getDetails(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("append_to_response") String credits
    );
}
