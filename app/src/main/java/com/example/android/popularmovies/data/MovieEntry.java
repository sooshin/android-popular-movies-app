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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "movie")
public class MovieEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "movie_id")
    private int movieId;

    private String title;
    private Date date;

    /**
     * Constructor
     *
     * @param movieId The movie ID
     * @param title Title of the movie
     * @param date Date the user added to the favorite list
     */
    @Ignore
    public MovieEntry(int movieId, String title, Date date) {
        this.movieId = movieId;
        this.title = title;
        this.date = date;
    }

    /**
     * Constructor used by Room to create MovieEntries
     */
    public MovieEntry(int id, int movieId, String title, Date date) {
        this.id = id;
        this.movieId = movieId;
        this.title = title;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }
}
