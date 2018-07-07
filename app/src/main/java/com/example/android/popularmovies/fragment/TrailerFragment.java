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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Video;
import com.example.android.popularmovies.model.VideoResponse;
import com.example.android.popularmovies.utilities.InjectorUtils;
import com.example.android.popularmovies.viewmodel.TrailerViewModel;
import com.example.android.popularmovies.viewmodel.TrailerViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;

public class TrailerFragment extends Fragment implements TrailerAdapter.TrailerAdapterOnClickHandler {

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

    /** ViewModel for TrailerFragment */
    private TrailerViewModel mTrailerViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public TrailerFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get movie data from the MainActivity
        mMovie = getMovieData();

        // Observe the data and update the UI
        setupViewModel(this.getActivity(), mMovie.getId());
    }

    /**
     * Every time the user data is updated, the onChanged callback will be invoked and update the UI
     */
    private void setupViewModel(Context context, int movieId) {
        TrailerViewModelFactory factory = InjectorUtils.provideTrailerViewModelFactory(context, movieId);
        mTrailerViewModel = ViewModelProviders.of(this, factory).get(TrailerViewModel.class);

        // Retrieve live data object using the getVideoResponse() method from the ViewModel
        mTrailerViewModel.getVideoResponse().observe(this, new Observer<VideoResponse>() {
            @Override
            public void onChanged(@Nullable VideoResponse videoResponse) {
                if (videoResponse != null) {
                    mVideos = videoResponse.getVideoResults();
                    videoResponse.setVideoResults(mVideos);

                    if (!mVideos.isEmpty()) {
                        mTrailerAdapter.addAll(mVideos);
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trailer, container, false);

        // Bind the view using ButterKnife
        mUnbinder = ButterKnife.bind(this, rootView);

        // A LinearLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a linear list.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Create an empty ArrayList
        mVideos = new ArrayList<>();

        // The TrailerAdapter is responsible for displaying each item in the list.
        mTrailerAdapter = new TrailerAdapter(mVideos, this);
        // Set TrailerAdapter on RecyclerView
        mRecyclerView.setAdapter(mTrailerAdapter);

        return rootView;
    }

    /**
     * Gets movie data from the MainActivity.
     */
    private Movie getMovieData() {
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
        return mMovie;
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

    /**
     * When a movie trailer selected, use an Intent to open a YouTube link
     *
     * @param videoUrl YouTube video url to display a trailer video
     */
    @Override
    public void onItemClick(String videoUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
        /*
         * This is a check we perform with every implicit Intent that we launch. In some cases,
         * the device where this code is running might not have an Activity to perform the action
         * with the data we've specified. Without this check, in those cases your app would crash.
         */
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
