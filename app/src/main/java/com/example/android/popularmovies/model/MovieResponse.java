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
