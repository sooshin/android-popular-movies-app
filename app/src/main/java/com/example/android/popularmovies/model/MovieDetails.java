package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetails {

    @SerializedName("budget")
    private int mBudget;

    @SerializedName("genres")
    private List<Genre> mGenres = null;

    @SerializedName("runtime")
    private int mRuntime;

    @SerializedName("revenue")
    private int mRevenue;

    @SerializedName("status")
    private String mStatus;

    @SerializedName("vote_count")
    private int mVoteCount;

    public int getBudget() {
        return mBudget;
    }

    public List<Genre> getGenres() {
        return mGenres;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public int getRevenue() {
        return mRevenue;
    }

    public String getStatus() {
        return mStatus;
    }

    public int getVoteCount() {
        return mVoteCount;
    }
}
