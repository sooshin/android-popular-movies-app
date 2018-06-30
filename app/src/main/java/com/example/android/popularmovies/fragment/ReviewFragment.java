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

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.ReviewResponse;
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
import static com.example.android.popularmovies.utilities.Constant.PAGE;

public class ReviewFragment extends Fragment implements Callback<ReviewResponse> {

    /** Tag for a log message */
    private static final String TAG = ReviewFragment.class.getSimpleName();

    /** Member variable for the list of reviews */
    private List<Review> mReviews;

    /** Get a reference to RecyclerView */
    @BindView(R.id.rv_review)
    RecyclerView mRecyclerView;

    /** Member variable for ReviewAdapter */
    private ReviewAdapter mReviewAdapter;

    private Unbinder mUnbinder;

    /** Member variable for the Movie object */
    private Movie mMovie;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public ReviewFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);

        // Bind the view using ButterKnife
        mUnbinder = ButterKnife.bind(this, rootView);

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

        // A LinearLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a linear list.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Create an empty ArrayList
        mReviews = new ArrayList<>();

        // The ReviewAdapter is responsible for displaying each item in the list.
        mReviewAdapter = new ReviewAdapter(mReviews);
        // Set ReviewAdapter on RecyclerView
        mRecyclerView.setAdapter(mReviewAdapter);

        callMovieReviews();
        return rootView;
    }

    /**
     * Makes a network request by calling enqueue
     */
    private void callMovieReviews() {
        // The Retrofit class generates an implementation of the TheMovieApi interface.
        Retrofit retrofit = Controller.getClient();
        TheMovieApi theMovieApi = retrofit.create(TheMovieApi.class);

        // Each call from the created TheMovieApi can make a synchronous or asynchronous HTTP request
        // to the remote web server. Send Request:
        // https://api.themoviedb.org/3/movie/{id}/reviews?api_key={API_KEY}&language=en-US&page=1
        Call<ReviewResponse> callReviewResponse = theMovieApi.getReviews(
                mMovie.getId(), API_KEY, LANGUAGE, PAGE);

        // Calls are executed with asynchronously with enqueue and notify callback of its response
        callReviewResponse.enqueue(this);
    }

    /**
     * Invoked for a received HTTP response.
     */
    @Override
    public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
        if(response.isSuccessful()) {
            ReviewResponse reviewResponse = response.body();
            if (reviewResponse != null) {
                // Get the list of reviews
                mReviews = reviewResponse.getReviewResults();
                reviewResponse.setReviewResults(mReviews);

                mReviewAdapter.addAll(mReviews);
            }
        }
    }

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected exception
     * occurred creating the request or processing the response.
     */
    @Override
    public void onFailure(Call<ReviewResponse> call, Throwable t) {
        Log.e(TAG, "onFailure: " + t.getMessage());
    }

    /**
     * When binding a fragment in onCreateView, set the views to null in onDestroyView.
     * Butter Knife returns an Unbinder instance when calling bind
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}
