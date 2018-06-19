package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

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
