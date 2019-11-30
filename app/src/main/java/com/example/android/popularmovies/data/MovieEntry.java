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

package com.example.android.popularmovies.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "movie")
public class MovieEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "movie_id")
    private int movieId;

    @ColumnInfo(name= "original_title")
    private String originalTitle;

    private String title;

    @ColumnInfo(name = "poster_path")
    private String posterPath;

    private String overview;

    @ColumnInfo(name = "vote_average")
    private double voteAverage;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    @ColumnInfo(name = "backdrop_path")
    private String backdropPath;

    private Date date;

    private String runtime;

    @ColumnInfo(name = "release_year")
    private String releaseYear;

    private String genre;

    /**
     * Constructor
     *
     * @param movieId The movie ID
     * @param title Title of the movie
     * @param date Date the user added to the favorite list
     */
    @Ignore
    public MovieEntry(int movieId, String originalTitle, String title, String posterPath, String overview,
                      double voteAverage, String releaseDate, String backdropPath, Date date,
                      String runtime, String releaseYear, String genre) {
        this.movieId = movieId;
        this.originalTitle = originalTitle;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.backdropPath = backdropPath;
        this.date = date;
        this.runtime = runtime;
        this.releaseYear = releaseYear;
        this.genre = genre;
    }

    /**
     * Constructor used by Room to create MovieEntries
     */
    public MovieEntry(int id, int movieId, String originalTitle, String title, String posterPath, String overview,
                      double voteAverage, String releaseDate, String backdropPath, Date date,
                      String runtime, String releaseYear, String genre) {
        this.id = id;
        this.movieId = movieId;
        this.originalTitle = originalTitle;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.backdropPath = backdropPath;
        this.date = date;
        this.runtime = runtime;
        this.releaseYear = releaseYear;
        this.genre = genre;
    }

    public int getId() {
        return id;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Date getDate() {
        return date;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public String getGenre() {
        return genre;
    }
}
