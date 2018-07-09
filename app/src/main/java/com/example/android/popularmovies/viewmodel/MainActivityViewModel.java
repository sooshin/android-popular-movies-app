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

package com.example.android.popularmovies.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmovies.data.MovieRepository;
import com.example.android.popularmovies.model.MovieResponse;

/**
 * {@link ViewModel} for MainActivity
 */
public class MainActivityViewModel extends ViewModel {

    private final MovieRepository mRepository;
    private LiveData<MovieResponse> mMovieResponse;

    public MainActivityViewModel(MovieRepository repository, String sortCriteria) {
        mRepository = repository;
        mMovieResponse = mRepository.getMovies(sortCriteria);
    }

    public LiveData<MovieResponse> getMovieResponse() {
        return mMovieResponse;
    }

    public void setMovieResponse(String sortCriteria) {
        mMovieResponse = mRepository.getMovies(sortCriteria);
    }
}
