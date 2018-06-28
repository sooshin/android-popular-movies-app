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

/**
 * A {@link Genre} object includes information related to the genre of the movie.
 */
public class Genre {

    @SerializedName("id")
    private int mGenreId;

    @SerializedName("name")
    private String mGenreName;

    public void setGenreId(int genreId) {
        mGenreId = genreId;
    }

    public int getGenreId() {
        return mGenreId;
    }

    public void setGenreName(String genreName) {
        mGenreName = genreName;
    }

    public String getGenreName() {
        return mGenreName;
    }
}
