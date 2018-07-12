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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

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
    @SerializedName("id")
    private int mId;

    /** Original title of the movie */
    @SerializedName("original_title")
    private String mOriginalTitle;

    /** Title of the movie */
    @SerializedName("title")
    private String mTitle;

    /** Movie poster image thumbnail */
    @SerializedName("poster_path")
    private String mPosterPath;

    /** Overview (or plot synopsis) of the movie */
    @SerializedName("overview")
    private String mOverview;

    /** Vote average (or user rating) of the movie */
    @SerializedName("vote_average")
    private double mVoteAverage;

    /** Release date of the movie */
    @SerializedName("release_date")
    private String mReleaseDate;

    /** Backdrop of the movie */
    @SerializedName("backdrop_path")
    private String mBackdropPath;

    public Movie(int movieId, String originalTitle, String title, String posterPath, String overview,
                 double voteAverage, String releaseDate, String backdropPath) {
        mId = movieId;
        mOriginalTitle = originalTitle;
        mTitle = title;
        mPosterPath = posterPath;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate =releaseDate;
        mBackdropPath = backdropPath;
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
    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    /**
     * Returns the title of the movie
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns a movie poster path
     */
    public String getPosterPath() {
        return mPosterPath;
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
    public String getReleaseDate() {
        return mReleaseDate;
    }

    /**
     * Returns a movie backdrop image path
     */
    public String getBackdropPath() {
        return mBackdropPath;
    }

    // Parcelling part
    /**
     * Read the value from the parcel
     * @param in Parcel object that contains flattened data
     */
    private Movie(Parcel in) {
        mId = in.readInt();
        mOriginalTitle = in.readString();
        mTitle = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
        mVoteAverage = in.readDouble();
        mReleaseDate = in.readString();
        mBackdropPath = in.readString();
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
        dest.writeString(mOriginalTitle);
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeDouble(mVoteAverage);
        dest.writeString(mReleaseDate);
        dest.writeString(mBackdropPath);
    }
}
