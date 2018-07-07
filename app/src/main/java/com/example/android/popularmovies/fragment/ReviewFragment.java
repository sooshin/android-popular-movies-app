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
import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.ReviewResponse;
import com.example.android.popularmovies.utilities.InjectorUtils;
import com.example.android.popularmovies.viewmodel.ReviewViewModel;
import com.example.android.popularmovies.viewmodel.ReviewViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;

public class ReviewFragment extends Fragment implements ReviewAdapter.ReviewAdapterOnClickHandler {

    /** Tag for a log message */
    private static final String TAG = ReviewFragment.class.getSimpleName();

    /** Member variable for the list of reviews */
    private List<Review> mReviews;

    /** Get a reference to RecyclerView */
    @BindView(R.id.rv_review)
    RecyclerView mRecyclerView;

    /** Get a reference to the TextView that displays a message saying that no reviews found */
    @BindView(R.id.tv_no_reviews)
    TextView mNoReviewsTextView;

    /** Member variable for ReviewAdapter */
    private ReviewAdapter mReviewAdapter;

    private Unbinder mUnbinder;

    /** Member variable for the Movie object */
    private Movie mMovie;

    /** ViewModel for ReviewFragment */
    private ReviewViewModel mReviewViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public ReviewFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

        // Observe the data and update the UI
        setupViewModel(this.getActivity());
    }

    /**
     * Every time the user data is updated, the onChanged callback will be invoked and update the UI
     */
    private void setupViewModel(Context context) {
        ReviewViewModelFactory factory = InjectorUtils.provideReviewViewModelFactory(context, mMovie.getId());
        mReviewViewModel = ViewModelProviders.of(this, factory).get(ReviewViewModel.class);

        mReviewViewModel.getReviewResponse().observe(this, new Observer<ReviewResponse>() {
            @Override
            public void onChanged(@Nullable ReviewResponse reviewResponse) {
                if (reviewResponse != null) {
                    // Get the list of reviews
                    mReviews = reviewResponse.getReviewResults();
                    reviewResponse.setReviewResults(mReviews);
                    if (!mReviews.isEmpty()) {
                        mReviewAdapter.addAll(mReviews);
                    } else {
                        // If there are no reviews, show a message that says no reviews found
                        showNoReviewsMessage();
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);

        // Bind the view using ButterKnife
        mUnbinder = ButterKnife.bind(this, rootView);

        // A LinearLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a linear list.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Create an empty ArrayList
        mReviews = new ArrayList<>();

        // The ReviewAdapter is responsible for displaying each item in the list.
        mReviewAdapter = new ReviewAdapter(mReviews, this);
        // Set ReviewAdapter on RecyclerView
        mRecyclerView.setAdapter(mReviewAdapter);

        return rootView;
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
     * Handles RecyclerView item clicks to open a website that displays the user review.
     *
     * @param url The URL that displays the user review
     */
    @Override
    public void onItemClick(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    /**
     * This method will make the message that says no reviews found visible and
     * hide the View for the review data
     */
    private void showNoReviewsMessage() {
        // First, hide the currently visible data
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Then, show a message that says no reviews found
        mNoReviewsTextView.setVisibility(View.VISIBLE);
    }
}
