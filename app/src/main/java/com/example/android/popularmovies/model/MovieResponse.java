package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * The {@link MovieResponse} object includes information related to page, total results, total pages,
 * and the list of Movies.
 */
public class MovieResponse {

    @SerializedName("page")
    private int mPage;

    @SerializedName("total_results")
    private int mTotalResults;

    @SerializedName("total_pages")
    private int mTotalPages;

    @SerializedName("results")
    private List<Movie> mMovieResults = null;

    @SuppressWarnings({"unused", "used by Retrofit"})
    public MovieResponse() {
    }

    public List<Movie> getMovieResults() {
        return mMovieResults;
    }
}
