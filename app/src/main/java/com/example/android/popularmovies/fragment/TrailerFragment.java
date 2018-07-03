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

package com.example.android.popularmovies.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Video;
import com.example.android.popularmovies.model.VideoResponse;
import com.example.android.popularmovies.utilities.Controller;
import com.example.android.popularmovies.utilities.TheMovieApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.android.popularmovies.utilities.Constant.API_KEY;
import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;
import static com.example.android.popularmovies.utilities.Constant.LANGUAGE;

public class TrailerFragment extends Fragment implements Callback<VideoResponse> {

    /** Tag for a log message */
    private static final String TAG = TrailerFragment.class.getSimpleName();

    /** Member variable for the list of trailers */
    private List<Video> mVideos;

    /** Member variable for TrailerAdapter */
    private TrailerAdapter mTrailerAdapter;

    private Unbinder mUnbinder;

    private Movie mMovie;

    /** Get a reference to RecyclerView */
    @BindView(R.id.rv_trailer)
    RecyclerView mRecyclerView;

    /** Get a reference to the TextView that displays a message saying that no trailers found */
    @BindView(R.id.tv_no_trailers)
    TextView mNoTrailersTextView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public TrailerFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trailer, container, false);

        // Bind the view using ButterKnife
        mUnbinder = ButterKnife.bind(this, rootView);

        // Get movie data from the MainActivity
        getMovieData();

        // A LinearLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a linear list.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Create an empty ArrayList
        mVideos = new ArrayList<>();

        // The TrailerAdapter is responsible for displaying each item in the list.
        mTrailerAdapter = new TrailerAdapter(mVideos);
        // Set TrailerAdapter on RecyclerView
        mRecyclerView.setAdapter(mTrailerAdapter);

        callMovieTrailers();
        return rootView;
    }

    /**
     * Gets movie data from the MainActivity.
     */
    private void getMovieData() {
        // Store the Intent
        Intent intent = getActivity().getIntent();
        // Check if the Intent is not null, and has the extra we passed from MainActivity
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                // Receive the Movie object which contains information, such as ID, original title,
                // poster path, overview, vote average, release date, backdrop path.
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }
    }

    /**
     * Makes a network request by calling enqueue
     */
    private void callMovieTrailers() {
        // The Retrofit class generates an implementation of the TheMovieApi interface.
        Retrofit retrofit = Controller.getClient();
        TheMovieApi theMovieApi = retrofit.create(TheMovieApi.class);

        // Each call from the created TheMovieApi can make a synchronous or asynchronous HTTP request
        // to the remote web server. Send Request:
        // https://api.themoviedb.org/3/movie/{id}/videos?api_key={API_KEY}&language=en-US
        Call<VideoResponse> callVideoResponse = theMovieApi.getVideos(
                mMovie.getId(), API_KEY, LANGUAGE);

        // Calls are executed with asynchronously with enqueue and notify callback of its response
        callVideoResponse.enqueue(this);
    }

    /**
     * Invoked for a received HTTP response.
     */
    @Override
    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
        if (response.isSuccessful()) {
            VideoResponse videoResponse = response.body();
            if (videoResponse != null) {
                mVideos = videoResponse.getVideoResults();
                videoResponse.setVideoResults(mVideos);

                if (!mVideos.isEmpty()) {
                    mTrailerAdapter.addAll(mVideos);
                }
            }
        }
    }

    @Override
    public void onFailure(Call<VideoResponse> call, Throwable t) {
        Log.e(TAG, "onFailure: " + t.getMessage());
    }

    /**
     * When binding a fragment in onCreateView, set the views to null in onDestroyView.
     * Butter Knife returns an Unbinder instance when calling bind.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
