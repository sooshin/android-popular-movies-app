package com.example.android.popularmovies.model;

/**
 * A {@link Movie} object includes information related to a movie.
 */
public class Movie {

    /** Title of the movie */
    private String mTitle;
    /** Movie poster image thumbnail */
    private String mThumbnail;
    /** Overview (or plot synopsis) of the movie */
    private String mOverview;
    /** Vote average (or user rating) of the movie */
    private double mVoteAverage;
    /** Release date of the movie */
    private String mReleaseDate;

    /**
     * Constructs a new {@link Movie} object
     *
     * @param title is the original title of the movie
     * @param thumbnail is movie poster image thumbnail
     * @param overview is a plot synopsis of the movie
     * @param voteAverage is user rating of the movie
     * @param releaseDate is the release date of the movie
     */
    public Movie(String title, String thumbnail, String overview, double voteAverage, String releaseDate) {
        mTitle = title;
        mThumbnail = thumbnail;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
    }

    /**
     * Returns the original title of the movie
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns a movie poster thumbnail image url
     */
    public String getThumbnail() {
        return mThumbnail;
    }

    /**
     * Returns a plot synopsis of the movie
     */
    public String getOverview() {
        return mOverview;
    }

    /**
     * Returns user rating of the movie
     */
    public double getVoteAverage() {
        return mVoteAverage;
    }

    /**
     * Returns release date of the movie
     */
    public String getReleaseData() {
        return mReleaseDate;
    }
}
