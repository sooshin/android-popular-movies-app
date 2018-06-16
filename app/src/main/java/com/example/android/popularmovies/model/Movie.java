package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A {@link Movie} object includes information related to a movie.
 * This class implements Parcelable interface to allow {@link Movie} object to be sent as a Parcel
 */
public class Movie implements Parcelable {

    // CREATOR implements the Parcelable.Creator interface
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        /**
         * Creates a new instance of Movie, instantiating it from the given Parcel whose data had
         * previously been written by Parcelable.writeToParcel().
         *
         * @param in The Parcel to read the movie object's data from
         * @return a new instance of Movie
         */
        public Movie createFromParcel(Parcel in){
            return new Movie(in);
        }

        /**
         * Creates a new array of Movie.
         *
         * @param size size of the array
         * @return an array of Movie, with every entry initialized to null
         */
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /** The id of the movie */
    private int mId;
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
    /** Backdrop of the movie */
    private String mBackdrop;

    /**
     * Constructs a new {@link Movie} object
     *
     * @param id is the id of the movie
     * @param title is the original title of the movie
     * @param thumbnail is movie poster image thumbnail
     * @param overview is a plot synopsis of the movie
     * @param voteAverage is user rating of the movie
     * @param releaseDate is the release date of the movie
     * @param backdrop is backdrop image of the movie
     */
    public Movie(int id, String title, String thumbnail, String overview, double voteAverage, String releaseDate, String backdrop) {
        mId = id;
        mTitle = title;
        mThumbnail = thumbnail;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
        mBackdrop = backdrop;
    }

    /**
     * Returns the id of the movie
     */
    public int getId() {
        return mId;
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

    /**
     * Returns a movie backdrop image
     */
    public String getBackdrop() {
        return mBackdrop;
    }

    // Parcelling part
    /**
     * Read the value from the parcel
     * @param in Parcel object that contains flattened data
     */
    private Movie(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mThumbnail = in.readString();
        mOverview = in.readString();
        mVoteAverage = in.readDouble();
        mReleaseDate = in.readString();
        mBackdrop = in.readString();
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable instance's marshaled
     * representation.
     *
     * @return a bitmask indicating the set of special object types marshaled by this Parcelable
     * object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten Movie object in to a Parcel
     *
     * @param dest The Parcel in which the object should be written
     * @param flags Additional flags about how the object should be written.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mThumbnail);
        dest.writeString(mOverview);
        dest.writeDouble(mVoteAverage);
        dest.writeString(mReleaseDate);
        dest.writeString(mBackdrop);
    }
}
