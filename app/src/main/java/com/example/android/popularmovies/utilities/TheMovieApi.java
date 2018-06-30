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

import com.example.android.popularmovies.model.MovieDetails;
import com.example.android.popularmovies.model.MovieResponse;
import com.example.android.popularmovies.model.ReviewResponse;
import com.example.android.popularmovies.model.VideoResponse;

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

    @GET("movie/{id}/reviews")
    Call<ReviewResponse> getReviews(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("movie/{id}/videos")
    Call<VideoResponse> getVideos(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

}
