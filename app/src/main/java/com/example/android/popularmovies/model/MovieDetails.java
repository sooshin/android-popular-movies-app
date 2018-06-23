package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetails {

    @SerializedName("budget")
    private long mBudget;

    @SerializedName("genres")
    private List<Genre> mGenres = null;

    @SerializedName("runtime")
    private int mRuntime;

    @SerializedName("revenue")
    private long mRevenue;

    @SerializedName("status")
    private String mStatus;

    @SerializedName("vote_count")
    private int mVoteCount;

    @SerializedName("credits")
    private Credits mCredits;

    public void setBudget(long budget) {
        mBudget = budget;
    }

    public long getBudget() {
        return mBudget;
    }

    public void setGenres(List<Genre> genres) {
        mGenres = genres;
    }

    public List<Genre> getGenres() {
        return mGenres;
    }

    public void setRuntime(int runtime) {
        mRuntime = runtime;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public void setRevenue(long revenue) {
        mRevenue = revenue;
    }

    public long getRevenue() {
        return mRevenue;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setVoteCount(int voteCount) {
        mVoteCount = voteCount;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    public void setCredits(Credits credits) {
        mCredits = credits;
    }

    public Credits getCredits() {
        return mCredits;
    }
}
